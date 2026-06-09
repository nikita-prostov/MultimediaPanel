using System.Reflection;
using VkNet.Model;

namespace MusicModule.Loader
{
    public static class ImageLoader
    {
        private static readonly HttpClient _httpClient = new();
        public static async Task LoadAsync(string savePath, string thumbUrl, long albumId)
        {
            if (string.IsNullOrEmpty(thumbUrl))
                return;

            try
            {
                if (thumbUrl.StartsWith("//"))
                    thumbUrl = "https:" + thumbUrl;
                else if (!thumbUrl.StartsWith("http"))
                    thumbUrl = "https://" + thumbUrl;

                string extension = GetExtensionFromUrl(thumbUrl);
                string filePath = Path.Combine(savePath, $"album_{albumId}{extension}");

                if (File.Exists(filePath) && new FileInfo(filePath).Length > 0)
                    return;

                Directory.CreateDirectory(savePath);

                var response = await _httpClient.GetAsync(thumbUrl);
                response.EnsureSuccessStatusCode();

                await using var stream = await response.Content.ReadAsStreamAsync();
                await using var fileStream = File.Create(filePath);
                await stream.CopyToAsync(fileStream);

                return;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка загрузки изображения: {ex.Message}");
                return;
            }
        }

        public static async Task<(byte[]? bytes, string? contentType)> GetImageAsync(string savePath, long albumId)
        {
            string[] extensions = { ".jpg", ".jpeg", ".png", ".webp", ".gif" };
            string? filePath = null;

            foreach (var ext in extensions)
            {
                string candidate = Path.Combine(savePath, $"album_{albumId}{ext}");
                if (System.IO.File.Exists(candidate))
                {
                    filePath = candidate;
                    break;
                }
            }

            if (filePath == null)
                return (null,null);

            string contentType = Path.GetExtension(filePath) switch
            {
                ".jpg" or ".jpeg" => "image/jpeg",
                ".png" => "image/png",
                ".webp" => "image/webp",
                ".gif" => "image/gif",
                _ => "application/octet-stream"
            };

            var bytes = await System.IO.File.ReadAllBytesAsync(filePath);
            return (bytes, contentType);
        }

        public static async Task<byte[]> GetPlaceholder()
        {
            var assembly = Assembly.GetExecutingAssembly();

            var resourceName = "MusicModule.Resources.placeholder.png";

            using var stream = assembly.GetManifestResourceStream(resourceName);
            if (stream == null)
                return [];
            var bytes = new byte[stream.Length];
            await stream.ReadExactlyAsync(bytes);
            return bytes;
        }

        private static string GetExtensionFromUrl(string url)
        {
            string path = url.Split('?')[0];
            string extension = Path.GetExtension(path)?.ToLower();

            if (!string.IsNullOrEmpty(extension) && extension is ".jpg" or ".jpeg" or ".png" or ".webp" or ".gif")
                return extension;

            return ".jpg";
        }
    }
}
