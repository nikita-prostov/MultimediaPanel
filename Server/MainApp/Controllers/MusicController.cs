using Microsoft.AspNetCore.Mvc;
using MusicModule.Enums;
using MusicModule.Loader;
using MusicModule.Services;
using System.Text.Json;

namespace MainApp.Controllers
{
    [ApiController]
    [Route("music")]
    public class MusicController(MusicService service, JsonSerializerOptions serializerOptions) : ControllerBase
    {
        [HttpGet("connect")]
        public async Task ConnectAsync(CancellationToken ct)
        {
            Console.WriteLine("MusicController: SSE client connected");
            Response.Headers.Append("Content-Type", "text/event-stream");
            Response.Headers.Append("Cache-Control", "no-cache");
            Response.Headers.Append("Connection", "keep-alive");
            try
            {
                while (!ct.IsCancellationRequested)
                {
                    var state = service.State;
                    if (state != null)
                    {
                        var json = JsonSerializer.Serialize(state, serializerOptions);
                        await Response.WriteAsync($"data: {json}\n\n", ct);
                        await Response.Body.FlushAsync(ct);
                    }
                    await Task.Delay(1000, ct);
                }
            }
            catch (TaskCanceledException)
            {
                Console.WriteLine("SSE client disconnected");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"SSE error: {ex.Message}");
            }
        }
        [HttpGet("search")]
        public async Task<IActionResult> Search([FromQuery] string query, [FromQuery] bool ignoreCase) => Ok(await service.SearchAsync(query, ignoreCase));
        

        [HttpGet("list")]
        public IActionResult GetList([FromQuery] int page = 1) => Ok(service.GetList(page));
        

        [HttpPost("load")]
        public async Task<IActionResult> LoadAsync([FromQuery] TracksSource? source, [FromQuery] int page = 1)
        {
            source ??= service.State.Source;
            await service.LoadAsync(source.Value, page);
            return Ok();
        }

        [HttpPost("add")]
        public async Task<IActionResult> AddAsync([FromQuery] long audioId, [FromQuery] long ownerId)
        {
            if (audioId <= 0 || ownerId == 0) return BadRequest("Invalid audioId or ownerId");

            await service.AddAsync(audioId, ownerId);
            return Ok();
        }

        [HttpDelete("delete")]
        public async Task<IActionResult> DeleteAsync([FromQuery] long audioId, [FromQuery] long ownerId)
        {
            if (audioId <= 0 || ownerId == 0) return BadRequest("Invalid audioId or ownerId");

            await service.DeleteAsync(audioId, ownerId);
            return Ok();
        }

        [HttpGet("thumb")]
        public async Task<IActionResult> GetThumbAsync([FromQuery] long albumId)
        {
            if (albumId == 0) return File(await ImageLoader.GetPlaceholder(), "image/png");

            var image = await service.GetThumbAsync(albumId);
            if(image.Item1 == null || image.Item2 == null) return File(await ImageLoader.GetPlaceholder(), "image/png");

            return File(image.Item1, image.Item2);
        }

        [HttpPost("shuffle")]
        public IActionResult Shuffle()
        {
            service.Shuffle();
            return Ok();
        }

        [HttpPost("sort")]
        public IActionResult Sort()
        {
            service.Sort();
            return Ok();
        }

        [HttpPost("repeat/set")]
        public IActionResult SetRepeatMode([FromQuery] RepeatMode repeatMode)
        {
            service.SetRepeatMode(repeatMode);
            return Ok();
        }

        [HttpPost("volume/set")]
        public IActionResult SetVolume([FromQuery] float value)
        {
            service.SetVolume(value);
            return Ok();
        }

        [HttpPost("seek/to")]
        public IActionResult SeekTo([FromQuery] int position)
        {
            service.SeekTo(position);
            return Ok();
        }

        [HttpPost("play")]
        public async Task<IActionResult> PlayAsync([FromQuery] int position = -1)
        {
            if (position < 0) await service.PlayAsync();
            else await service.PlayAsync(position);
            return Ok();
        }

        [HttpPost("play/track")]
        public async Task<IActionResult> PlayTrackAsync([FromQuery] long audioId, [FromQuery] long ownerId)
        {
            await service.PlayTrackAsync(audioId,ownerId);
            return Ok();
        }

        [HttpPost("pause")]
        public async Task<IActionResult> Pause()
        {
            service.Pause();
            return Ok();
        }

        [HttpPost("play/next")]
        public async Task<IActionResult> PlayNextAsync()
        {
            await service.PlayNextAsync();
            return Ok();
        }

        [HttpPost("play/prev")]
        public async Task<IActionResult> PlayPrevAsync()
        {
            await service.PlayPrevAsync();
            return Ok();
        }
    }
}