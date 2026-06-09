using MusicModule.AudioPlayer;
using MusicModule.Enums;
using MusicModule.Loader;
using MusicModule.Models;
using System;
using System.Collections.Generic;
using System.Text;
using VkNet;
using VkNet.Model;

namespace MusicModule.Services
{
    public sealed class MusicService
    {
        private readonly Player player;
        private int current = 0;
        private bool isLocked = false;
        private readonly PlayerState state;
        private readonly List<AudioTrack> tracks;
        private readonly VkApi? vkApi;
        private bool useShuffled = false;
        private List<AudioTrack> shuffledTracks = [];
        private readonly string savePath;
        private readonly string cachePath;
        private readonly string savePathThumb;
        private readonly string cachePathThumb;
        private readonly string accessToken;

        public MusicService(string savePath, string cachePath,float initVolume, List<AudioTrack> tracks, VkApi? api = null, TracksSource source = TracksSource.MyMusic, string accessToken = "")
        {
            this.savePath = Path.Combine(savePath, "Musics");
            this.cachePath = Path.Combine(cachePath, "Musics");
            savePathThumb = Path.Combine(savePath, "Thumbs");
            cachePathThumb = Path.Combine(cachePath, "Thumbs");
            this.accessToken = accessToken;
            player = new Player(initVolume);
            state = new PlayerState
            {
                Volume = initVolume,
            };
            this.tracks = tracks;

            player.PlayerStopped += Player_PlayerStopped;

            if(api != null)
                vkApi = api;

            state.Source = source;
        }

        public PlayerState State
        {
            get
            {
                UpdateState();
                return state;
            }
        }

        public async Task PlayAsync(int pos = -1)
        {
            if (isLocked) return;

            string saveTo = state.Source == TracksSource.Recomendations ? cachePath : savePath;
            string saveThumbTo = state.Source == TracksSource.Recomendations ? cachePathThumb : savePathThumb;
            if (pos >= 0 && pos != current)
            {
                current = pos;
                isLocked = true;
                var currentTrack = useShuffled ? shuffledTracks[current] : tracks[current];
                await ImageLoader.LoadAsync(saveThumbTo, currentTrack.OriginalThumbUrl, currentTrack.AlbumId);
                var file = await Converter.ConvertAsync(saveTo, currentTrack.AudioUrl, currentTrack.Title, currentTrack.Artist);
                await player.PlayAsync(file);
                state.Track = currentTrack;
                state.Position = 0;
                isLocked = false;
            }

            if (player.IsPaused)
            {
                await player.PlayAsync();
                return;
            }

            if (!player.IsPlaying)
            {
                isLocked = true;
                var currentTrack = useShuffled ? shuffledTracks[current] : tracks[current];
                var file = await Converter.ConvertAsync(saveTo, currentTrack.AudioUrl, currentTrack.Title, currentTrack.Artist);
                await ImageLoader.LoadAsync(saveThumbTo, currentTrack.OriginalThumbUrl, currentTrack.AlbumId);
                await player.PlayAsync(file);
                state.Track = currentTrack;
                state.Position = 0;
                isLocked = false;
            }
            UpdateState();
        }

        public async Task PlayNextAsync()
        {
            player.Stop();
            current++;
            if (current >= tracks.Count) current = 0;
            await PlayAsync();
        }

        public async Task PlayPrevAsync()
        {
            player.Stop();
            current--;
            if (current < 0) current = tracks.Count - 1;
            await PlayAsync();
        }

        public void Pause() => player.Pause();

        public void SetVolume(float volume) => player.Volume = volume;

        public void SeekTo(int pos) => player.Position = pos;

        public void SetRepeatMode(RepeatMode mode) => state.RepeatMode = mode;

        public async Task SetSourceAsync(TracksSource source)
        {
            if (state.Source == source) return;

            if (source == TracksSource.MyMusic || source == TracksSource.Recomendations && vkApi == null) return;

            tracks.Clear();
            tracks.AddRange(await TrackLoader.LoadAsync(source, vkApi));
        }

        public void Shuffle()
        {
            player.Stop();
            current = 0;
            shuffledTracks.Clear();
            shuffledTracks.AddRange(tracks);
            Random rng = new Random();
            int n = shuffledTracks.Count;
            useShuffled = true;
            while (n > 1)
            {
                n--;
                int k = rng.Next(n + 1);
                (shuffledTracks[k], shuffledTracks[n]) = (shuffledTracks[n], shuffledTracks[k]);
            }
        }

        public void Sort()
        {
            player.Stop();
            current = 0;
            shuffledTracks.Clear();
            useShuffled = false;
        }

        public List<AudioTrack> GetList(int page)
        {
            var pageSize = 100;
            var list = useShuffled ? shuffledTracks : tracks;
            return [.. list.Skip((page - 1) * pageSize).Take(pageSize)];
        }

        public async Task<(byte[]?,string?)> GetThumbAsync(long albumId)
        {
            string saveThumbTo = state.Source == TracksSource.Recomendations ? cachePathThumb : savePathThumb;
            return await ImageLoader.GetImageAsync(saveThumbTo, albumId);
        }

        public async Task AddAsync(long audioId, long ownerId)
        {
            if (vkApi == null) return;

            var list = useShuffled ? shuffledTracks : tracks;
            var track = list.FirstOrDefault(t => t.Id == audioId);
            if (track == null) return;

            try
            {
                var result = await vkApi.Audio.AddAsync(audioId, ownerId);
                Console.WriteLine($"Audio {ownerId}_{audioId} success added");
                track.IsAdded = true;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Failed to add audio: {ex.Message}");
            }
        }

        public async Task DeleteAsync(long audioId, long ownerId)
        {
            if (vkApi == null) return;

            var list = useShuffled ? shuffledTracks : tracks;
            var track = list.FirstOrDefault(t => t.Id == audioId);
            if (track == null) return;

            var result = await vkApi.Audio.DeleteAsync(audioId, ownerId);
            if (result)
            {
                Console.WriteLine($"Audio by Id: {audioId} success deleted");
                track.IsAdded = false;
            }
        }

        public async Task LoadAsync(TracksSource source, int page)
        {
            if (state.Source != source)
            {
                tracks.Clear();
                shuffledTracks.Clear();
                current = 0;
                player.Stop();
                useShuffled = false;
                state.Source = source;
            }

            var newTracks = await TrackLoader.LoadAsync(source, vkApi, page);

            if (newTracks.Count == 0) return;

            tracks.AddRange(newTracks);

            if (useShuffled)
            {
                var shuffledNewTracks = new List<AudioTrack>(newTracks);
                Random rng = new Random();
                int n = shuffledNewTracks.Count;
                while (n > 1)
                {
                    n--;
                    int k = rng.Next(n + 1);
                    (shuffledNewTracks[k], shuffledNewTracks[n]) = (shuffledNewTracks[n], shuffledNewTracks[k]);
                }
                shuffledTracks.AddRange(shuffledNewTracks);
            }
        }

        private void UpdateState()
        {
            state.Track = useShuffled ? shuffledTracks[current] : tracks[current];
            state.Position = player.Position;
            state.Volume = player.Volume;
            state.IsPlaying = player.IsPlaying && !isLocked;
            state.IsShuffled = useShuffled;
            state.IsLoading = isLocked;
        }

        private void Player_PlayerStopped()
        {
            Task.Run(async () => {
                switch (state.RepeatMode)
                {
                    case RepeatMode.None:
                        {
                            player.Stop();
                            break;
                        }
                    case RepeatMode.RepeatCurrent:
                        {
                            player.Position = 0;
                            await player.PlayAsync();
                            break;
                        }
                    case RepeatMode.PlayNext:
                        {
                            await PlayNextAsync();
                            break;
                        }
                }
            });
        }
    }
}
