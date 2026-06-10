using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using NotificationsModule.Data;
using NotificationsModule.Enums;
using NotificationsModule.Models;
using NotificationsModule.Services;
using System.Text.Json;

namespace MainApp.Controllers
{
    [Route("notifications")]
    [ApiController]
    public class NotificationController(NotificationService service) : ControllerBase
    {
        NotificationDto? lastSentNotification;
        [HttpGet("subscribe")]
        public async Task ConnectAsync(CancellationToken ct)
        {
            Response.Headers.Append("Content-Type", "text/event-stream");
            Response.Headers.Append("Cache-Control", "no-cache");
            Response.Headers.Append("Connection", "keep-alive");

            try
            {
                while (!ct.IsCancellationRequested)
                {
                    var lastNotification = service.LastNotification;
                    if (lastNotification != null && lastNotification != lastSentNotification)
                    {
                        lastSentNotification = lastNotification;
                        var json = JsonSerializer.Serialize(lastNotification);
                        await Response.WriteAsync(json, ct);
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

        [HttpGet("list/all")]
        public async Task<IActionResult> GetListAsync([FromQuery] int page = 1)
        {
            if(page <= 0) page = 1;
            return Ok(await service.GetListAsync(page));
        }

        [HttpGet("list/filtered")]
        public async Task<IActionResult> GetFilteredListAsync([FromQuery] int page = 1, [FromQuery] DateTime? from = null, [FromQuery] DateTime? to = null,[FromQuery] NotificationType type = NotificationType.None, [FromQuery] string? title = null)
        {
            if (page <= 0) page = 1;
            return Ok(await service.GetFilteredListAsync(page, from,to, type, title));
        }
    }
}
