using CommonDataModule.Services;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Text.Json;

namespace MainApp.Controllers
{
    [Route("common")]
    [ApiController]
    public class CommonDataController(CommonDataService service, JsonSerializerOptions serializerOptions) : ControllerBase
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
                    var state = service.CommonData;
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
    }
}
