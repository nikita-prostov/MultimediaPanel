using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Text.Json;
using TransportInfoModule.Services;

namespace MainApp.Controllers
{
    [Route("transport")]
    [ApiController]
    public class TransportInfoController(TransportInfoService service, JsonSerializerOptions serializerOptions) : ControllerBase
    {
        [HttpGet("connect")]
        public async Task ConnectAsync(CancellationToken ct)
        {
            Response.Headers.Append("Content-Type", "text/event-stream");
            Response.Headers.Append("Cache-Control", "no-cache");
            Response.Headers.Append("Connection", "keep-alive");

            try
            {
                while (!ct.IsCancellationRequested)
                {
                    var state = service.TransportInfo;
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

        [HttpGet("logs")]
        public async Task<IActionResult> GetLogsAsync([FromQuery] DateTime? from = null, [FromQuery] DateTime? to = null, [FromQuery] bool activeOnly = false)
        {
            return Ok(service.GetFullLogs(to, from, activeOnly));
        }
    }
}
