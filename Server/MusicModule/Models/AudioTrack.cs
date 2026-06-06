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
        public string ThumbUrl { get; set; }
        public bool IsAdded { get; set; } = true;

        [JsonIgnore]
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
                    ThumbUrl = $"/music/thumb?albumId={audio.Album.Id}";
                    AlbumId = audio.Album.Id;
                }
                else
                    ThumbUrl = "/music/thumb?albumId=0";
            }
            else
            {
                Album = "None";
                ThumbUrl = "/music/thumb?albumId=0";
            }
            OwnerId = audio.OwnerId.Value;
            IsAdded = audio.OwnerId == userId;
            Duration = audio.Duration;
            AudioUrl = audio.Url.ToString();
        }
    }
}
