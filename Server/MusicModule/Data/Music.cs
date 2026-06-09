using System;
using System.Collections.Generic;
using System.Text;
using VkNet.Model;

namespace MusicModule.Data
{
    public class Music
    {
        public long Id { get; set; }
        public long TrackId { get; set; }
        public long OwnerId { get; set; }
        public string Title { get; set; }
        public string Artist { get; set; }
        public string Album { get; set; }
        public int Duration { get; set; }
        public long AlbumId { get; set; }
    }
}
