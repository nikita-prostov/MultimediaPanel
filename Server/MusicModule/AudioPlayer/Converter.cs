using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using Utils;
using VkNet.Model;

namespace MusicModule.AudioPlayer
{
    public static class Converter
    {
        public static async Task<string> ConvertAsync(string savePath, string url, long audioId, long ownerId)
        {
            var fileName = $"{audioId}_{ownerId}.mp3";
            var file = Path.Combine(savePath, fileName);

            if (File.Exists(file) && new FileInfo(file).Length > 0)
                return file;

            Directory.CreateDirectory(savePath);

            Console.WriteLine("Converting to mp3...");

            var process = new Process
            {
                StartInfo = new ProcessStartInfo
                {
                    FileName = "ffmpeg",
                    Arguments = $"-i \"{url}\" -acodec libmp3lame -ab 320k -y \"{file}\"",
                    UseShellExecute = false,
                    RedirectStandardOutput = true,   // ← Меняем на true, чтобы видеть вывод
                    RedirectStandardError = true,    // ← Меняем на true, чтобы видеть ошибки
                    CreateNoWindow = true
                }
            };

            process.Start();

            // Читаем вывод асинхронно
            var outputTask = process.StandardOutput.ReadToEndAsync();
            var errorTask = process.StandardError.ReadToEndAsync();

            await process.WaitForExitAsync();

            var output = await outputTask;
            var error = await errorTask;

            Console.WriteLine($"FFmpeg exit code: {process.ExitCode}");

            if (!string.IsNullOrEmpty(error))
                Console.WriteLine($"FFmpeg error: {error}");

            if (process.ExitCode == 0 && File.Exists(file) && new FileInfo(file).Length > 0)
            {
                Console.WriteLine("Success converted.");
                return file;
            }

            throw new IOException($"FFmpeg failed. Exit code: {process.ExitCode}. Error: {error}");
        }
    }
}
