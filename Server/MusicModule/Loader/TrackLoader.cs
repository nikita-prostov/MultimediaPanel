using Microsoft.EntityFrameworkCore;
using MusicModule.Data;
using MusicModule.Enums;
using MusicModule.Models;
using System;
using System.Collections.Generic;
using System.Text;
using VkNet;
using VkNet.Enums;
using VkNet.Enums.Filters;
using VkNet.Model;

namespace MusicModule.Loader
{
    public static class TrackLoader
    {
        public static async Task<List<AudioTrack>> LoadAsync(TracksSource source, VkApi? api = null, MusicDbContext? dbContext = null, string? path = null, int page = 1,List<AudioTrack>? myTracks = null)
        {
            List<AudioTrack> tracks = [];
            switch (source)
            {
                case TracksSource.MyMusic:
                    {
                        ArgumentNullException.ThrowIfNull(api);

                        Console.WriteLine("Loading you tracks from VK Music...");
                        tracks = await LoadMyMusicAsync(api);
                        break;
                    }
                case TracksSource.Recomendations:
                    {
                        ArgumentNullException.ThrowIfNull(api);

                        Console.WriteLine("Loading you tracks from recomendations...");
                        tracks = await LoadFromRecomenndationsAsync(api, myTracks ?? []);
                        break;
                    }
                case TracksSource.Local:
                    {
                        ArgumentNullException.ThrowIfNull(dbContext);
                        ArgumentNullException.ThrowIfNull(path);

                        Console.WriteLine("Loading you tracks from local cache...");
                        tracks = await LoadFromLocalCacheAsync(dbContext,path, page);
                        break;
                    }
            }
            Console.WriteLine("You tracks success loaded");
            return tracks;
        }

        private static async Task<List<AudioTrack>> LoadMyMusicAsync(VkApi api)
        {
            if (!api.IsAuthorized || !api.UserId.HasValue)
            {
                Console.WriteLine("Authorization error...");
                return [];
            }

            var res = new List<AudioTrack>();
            long offset = 0;

            var music = await api.Audio.GetAsync(new AudioGetParams
            {
                OwnerId = api.UserId.Value,
                Offset = offset,
                Count = 5999
            });

            foreach (var track in music)
            {
                if (track.Url != null)
                    res.Add(new AudioTrack(track, api.UserId.Value));
            }

            return res;
        }

        private static async Task<List<AudioTrack>> LoadFromRecomenndationsAsync(VkApi api,List<AudioTrack> myTracks)
        {
            if (!api.IsAuthorized || !api.UserId.HasValue)
            {
                Console.WriteLine("Authorization error...");
                return [];
            }

            var music = await api.Audio.GetRecommendationsAsync(userId:api.UserId.Value);
            var res = new List<AudioTrack>();
            foreach (var track in music)
            {
                var audioTrack = new AudioTrack(track, api.UserId.Value);
                res.Add(audioTrack);
            }
            return res;
        }
        private static async Task<List<AudioTrack>> LoadFromLocalCacheAsync(MusicDbContext dbContext, string path, int page = 1)
        {
            return await dbContext.Musics
                .Skip((page - 1)*100)
                .Take(100)
                .Select(m => new AudioTrack(m, path))
                .ToListAsync();
        }
    }
}
