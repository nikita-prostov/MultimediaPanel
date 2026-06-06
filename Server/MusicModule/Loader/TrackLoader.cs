using MusicModule.Enums;
using MusicModule.Models;
using System;
using System.Collections.Generic;
using System.Text;
using VkNet;
using VkNet.Enums.Filters;
using VkNet.Model;

namespace MusicModule.Loader
{
    public static class TrackLoader
    {
        public static async Task<List<AudioTrack>> LoadAsync(TracksSource source, VkApi? api = null, int page = 1)
        {
            List<AudioTrack> tracks = [];
            switch (source)
            {
                case TracksSource.MyMusic:
                    {
                        ArgumentNullException.ThrowIfNull(api);

                        Console.WriteLine("Loading you tracks from VK Music...");
                        tracks = await LoadMyMusicAsync(api, page);
                        break;
                    }
                case TracksSource.Recomendations:
                    {
                        ArgumentNullException.ThrowIfNull(api);

                        Console.WriteLine("Loading you tracks from recomendations...");
                        tracks = await LoadFromRecomenndationsAsync(api);
                        break;
                    }
                case TracksSource.Local:
                    {
                        throw new NotImplementedException();
                    }
            }
            Console.WriteLine("You tracks success loaded");
            return tracks;
        }

        private static async Task<List<AudioTrack>> LoadMyMusicAsync(VkApi api, int page = 1)
        {
            if (!api.IsAuthorized || !api.UserId.HasValue)
            {
                Console.WriteLine("Authorization error...");
                return [];
            }

            var res = new List<AudioTrack>();
            int batchSize = 100;
            long offset = (page - 1) * batchSize;

            var music = await api.Audio.GetAsync(new AudioGetParams
            {
                OwnerId = api.UserId.Value,
                Count = batchSize,
                Offset = offset
            });

            foreach (var track in music)
            {
                if (track.Url != null)
                    res.Add(new AudioTrack(track, api.UserId.Value));
            }

            return res;
        }

        private static async Task<List<AudioTrack>> LoadFromRecomenndationsAsync(VkApi api)
        {
            if (!api.IsAuthorized || !api.UserId.HasValue)
            {
                Console.WriteLine("Authorization error...");
                return [];
            }

            var music = await api.Audio.GetRecommendationsAsync(userId:api.UserId.Value, shuffle: false, count: 100,offset:0);
            var res = new List<AudioTrack>();
            foreach (var track in music)
            {
                res.Add(new(track,api.UserId.Value));
            }
            return res;
        }
    }
}
