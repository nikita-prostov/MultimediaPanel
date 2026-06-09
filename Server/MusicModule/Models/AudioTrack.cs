using MusicModule.Data;
using System.Text.Json.Serialization;
using VkNet.Model;

namespace MusicModule.Models
{
    public class AudioTrack()
    {
        public long Id { get; set; }
        public long OwnerId { get; set; }
        public string Title { get; set; }
        public string Artist { get; set; }
        public string Album { get; set; }
        public int Duration { get; set; }
        public bool IsAdded { get; set; } = true;
        public long AlbumId { get; set; }

        [JsonIgnore]
        public string AudioUrl { get; set; }

        [JsonIgnore]
        public string OriginalThumbUrl { get; set; }

        public AudioTrack(Audio audio, long userId) : this()
        {
            Id = audio.Id.Value;
            Title = audio.Title;
            Artist = audio.Artist;
            if (audio.Album != null)
            {
                Album = audio.Album.Title;
                if (audio.Album.Thumb != null)
                {
                    OriginalThumbUrl = audio.Album.Thumb.Photo300;
                    AlbumId = audio.Album.Id;
                }
            }
            else
            {
                Album = "None";
            }
            OwnerId = audio.OwnerId.Value;
            IsAdded = audio.OwnerId == userId;
            Duration = audio.Duration;
            AudioUrl = audio.Url.ToString();
        }

        public AudioTrack(Music music, string path) : this()
        {
            Id = music.TrackId;
            Title = music.Title;
            Artist = music.Artist;
            Album = music.Album;
            OwnerId = music.OwnerId;
            IsAdded = true;
            Duration = music.Duration;
            AlbumId = music.AlbumId;
            AudioUrl = $"file://{path}/{Id}_{OwnerId}.mp3";
        }
    }
}
