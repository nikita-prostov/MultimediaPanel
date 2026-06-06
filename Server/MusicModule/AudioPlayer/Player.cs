using NAudio.Wave;

namespace MusicModule.AudioPlayer
{
    public class Player(float volume)
    {
        private WaveOutEvent? outputDevice;
        private Mp3FileReader? reader;
        private string? currentFile = null;
        public bool IsPlaying { 
            get
            {
                if (outputDevice != null)
                    return outputDevice.PlaybackState == PlaybackState.Playing;
                else
                    return false;
            }
        }

        public bool IsPaused => outputDevice?.PlaybackState == PlaybackState.Paused;

        public float Volume
        {
            get => volume;

            set
            {
                volume = Math.Clamp(value, 0f, 1f);
                outputDevice?.Volume = volume;
            }
        }

        public int Position
        {
            get
            {
                if(reader != null)
                    return (int)(reader.CurrentTime.TotalSeconds);
                else
                    return 0;
            }

            set
            {
                if (reader != null && value >= 0 && value < reader.TotalTime.TotalSeconds)
                {
                    reader.CurrentTime = TimeSpan.FromSeconds(value);
                }
            }
        }

        public event Action? TrackStarted;
        public event Action? PlayerStopped;
        public event Action? TrackPaused;
        public event Action? TrackContinue;

        public async Task PlayAsync(string? filePath = null)
        {
            if (filePath != null)
            {
                Stop();
                reader = new Mp3FileReader(filePath);
                outputDevice = new WaveOutEvent { Volume = volume };
                outputDevice.PlaybackStopped += OnPlayerStopped;
                outputDevice.Init(reader);
                outputDevice.Play();
                currentFile = filePath;
                TrackStarted?.Invoke();
            }
            else if (currentFile != null)
            {
                outputDevice?.Play();
                TrackContinue?.Invoke();
            }
            else
            {
                Console.WriteLine("PlayAsync: no file to play!");
            }
        }

        public void Pause()
        {
            outputDevice?.Pause();
            TrackPaused?.Invoke();
        }

        public void Stop()
        {
            if (outputDevice != null)
            {
                outputDevice.PlaybackStopped -= OnPlayerStopped;
                outputDevice.Stop();
                outputDevice.Dispose();
                outputDevice = null;
            }

            reader?.Dispose();
            reader = null;
        }

        private void OnPlayerStopped(object? sender, StoppedEventArgs e)
        {
            PlayerStopped?.Invoke();
        }
    }
}
