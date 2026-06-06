using MusicModule.Enums;
using System;
using System.Collections.Generic;
using System.Text;

namespace MusicModule.Models
{
    public class PlayerState
    {
        public int Position { get; set; } = 0;
        public float Volume { get; set; } = 1;
        public bool IsPlaying { get; set; } = false;
        public bool IsShuffled { get; set; } = false;
        public bool IsLoading { get; set; } = false;
        public AudioTrack? Track { get; set; } = null;
        public RepeatMode RepeatMode { get; set; } = RepeatMode.None;
        public TracksSource Source { get; set; } = TracksSource.MyMusic;
    }
}
