using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using MusicModule.AudioPlayer;
using MusicModule.Data;
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
        private readonly IServiceScopeFactory scopeFactory;
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

        public MusicService(IServiceScopeFactory scopeFactory, string savePath, string cachePath,float initVolume, List<AudioTrack> tracks, VkApi? api = null, TracksSource source = TracksSource.MyMusic, string accessToken = "")
        {
            this.scopeFactory = scopeFactory;
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

        public async Task PlayTrackAsync(long audioId, long ownerId)
        {
            if(vkApi != null)
            {
                isLocked = true;
                var track = (await vkApi.Audio.GetByIdAsync([.. new List<string> { $"{ownerId}_{audioId}" }])).ToList()[0];
                await ImageLoader.LoadAsync(cachePathThumb, track.Album.Thumb.Photo300, track.Album.Id);
                var file = await Converter.ConvertAsync(cachePath, track.Url.ToString(), track.Id.Value, track.OwnerId.Value);
                await player.PlayAsync(file);
                state.Track = new AudioTrack(track, vkApi.UserId.Value);
                state.Position = 0;
                isLocked = false;
            }
        }

        public async Task PlayAsync(int pos = -1)
        {
            if (isLocked) return;

            string saveTo = state.Source == TracksSource.Recommendations ? Path.Combine(cachePath, "Recomendations") : savePath;
            string saveThumbTo = state.Source == TracksSource.Recommendations ? Path.Combine(cachePathThumb, "Recomendations") : savePathThumb;
            if (pos >= 0 && pos != current)
            {
                current = pos;
                isLocked = true;
                var currentTrack = useShuffled ? shuffledTracks[current] : tracks[current];
                await ImageLoader.LoadAsync(saveThumbTo, currentTrack.OriginalThumbUrl, currentTrack.AlbumId);
                var file = await Converter.ConvertAsync(saveTo, currentTrack.AudioUrl, currentTrack.Id, currentTrack.OwnerId);
                await player.PlayAsync(file);
                state.Track = currentTrack;
                state.Position = 0;
                isLocked = false;
                await SaveAsync(currentTrack);
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
                var file = await Converter.ConvertAsync(saveTo, currentTrack.AudioUrl, currentTrack.Id, currentTrack.OwnerId);
                await ImageLoader.LoadAsync(saveThumbTo, currentTrack.OriginalThumbUrl, currentTrack.AlbumId);
                await player.PlayAsync(file);
                state.Track = currentTrack;
                state.Position = 0;
                isLocked = false;
                await SaveAsync(currentTrack);
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

            if (source == TracksSource.MyMusic || source == TracksSource.Recommendations && vkApi == null) return;

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

        public GetListResponse GetList(int page)
        {
            var pageSize = 100;
            var list = useShuffled ? shuffledTracks : tracks;
            return new GetListResponse
            {
                Page = page,
                TotalPages = (int)MathF.Ceiling((float)list.Count / pageSize),
                Tracks = [.. list.Skip((page - 1) * pageSize).Take(pageSize)]
            };
        }

        public async Task<List<AudioTrack>> SearchAsync(string query, bool ignoreCase)
        {
            var comparison = ignoreCase
                ? StringComparison.CurrentCultureIgnoreCase
                : StringComparison.CurrentCulture;

            var findedTracks = new List<AudioTrack>();

            if (vkApi != null)
            {
                var res = await vkApi.Audio.SearchAsync(new AudioSearchParams
                {
                    SearchOwn = false,
                    Query = ignoreCase ? query.ToLower() : query
                });
                foreach (var track in res)
                {
                    if (track.Url != null)
                        findedTracks.Add(new AudioTrack(track, vkApi.UserId.Value));
                }
            }

            findedTracks.AddRange(tracks.Where(t => t.Artist.Contains(query, comparison) || t.Title.Contains(query, comparison)));

            var uniqueTracks = findedTracks.GroupBy(t => t.FullId).Select(g => g.First()).ToList();

            return uniqueTracks;
        }

        public async Task<(byte[]?,string?)> GetThumbAsync(long albumId)
        {
            string saveThumbTo = state.Source == TracksSource.Recommendations ? cachePathThumb : savePathThumb;
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
                track.IsAdded = true;
                track.OwnerId = vkApi.UserId.Value;
                track.Id = result;
                await SaveAsync(track);
                Console.WriteLine($"Audio {ownerId}_{audioId} success added");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Failed to add audio: {ex.Message}");
            }
        }

        public async Task DeleteAsync(long audioId, long ownerId)
        {
            if (vkApi == null) return;
            using var scope = scopeFactory.CreateScope();
            var dbContext = scope.ServiceProvider.GetRequiredService<MusicDbContext>();
            var list = useShuffled ? shuffledTracks : tracks;
            var track = list.FirstOrDefault(t => t.Id == audioId);
            if (track == null) return;

            var result = await vkApi.Audio.DeleteAsync(audioId, ownerId);
            if (result)
            {
                Console.WriteLine($"Audio by Id: {audioId} success deleted");
                track.IsAdded = false;
                await dbContext.Musics.Where(m => m.TrackId == audioId && m.OwnerId == ownerId).ExecuteDeleteAsync();
            }
        }

        public async Task LoadAsync(TracksSource source, int page)
        {
            using var scope = scopeFactory.CreateScope();
            var dbContext = scope.ServiceProvider.GetRequiredService<MusicDbContext>();
            if (state.Source != source)
            {
                tracks.Clear();
                shuffledTracks.Clear();
                current = 0;
                player.Stop();
                useShuffled = false;
                state.Source = source;
            }

            var newTracks = await TrackLoader.LoadAsync(source, vkApi, dbContext, savePath, page, myTracks: tracks);

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

        private async Task SaveAsync(AudioTrack audio)
        {
            if (audio == null) return;

            using var scope = scopeFactory.CreateScope();
            var db = scope.ServiceProvider.GetRequiredService<MusicDbContext>();

            var exists = await db.Musics
                .AnyAsync(m => m.TrackId == audio.Id && m.OwnerId == audio.OwnerId);

            if (!exists)
            {
                var music = new Music 
                {
                    TrackId = audio.Id,
                    OwnerId = audio.OwnerId,
                    Album = audio.Album,
                    AlbumId = audio.AlbumId,
                    Title = audio.Title,
                    Artist = audio.Artist,
                    Duration = audio.Duration,
                };
                db.Add(music);
                await db.SaveChangesAsync();
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
