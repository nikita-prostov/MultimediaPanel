using JobModule.Data;
using JobModule.Services;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Text.Json;

namespace MainApp.Controllers
{
    [Route("job")]
    [ApiController]
    public class JobController(JobService service, JsonSerializerOptions serializerOptions) : ControllerBase
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
                    var state = service.CurrentJobInfo;
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

        [HttpGet("list")]
        public IActionResult GetListAsync([FromServices] JobDbContext dbContext, [FromQuery] int page = 1)
        {
            var list = service.GetAll(dbContext,page);
            return Ok(list);
        }
    }
}
