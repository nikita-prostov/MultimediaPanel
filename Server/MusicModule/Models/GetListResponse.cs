using System;
using System.Collections.Generic;
using System.Text;

namespace MusicModule.Models
{
    public class GetListResponse
    {
        public List<AudioTrack> Tracks { get; set; }
        public int Page { get; set; }
        public int TotalPages { get; set; }
    }
}
