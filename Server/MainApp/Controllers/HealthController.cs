using Microsoft.AspNetCore.Mvc;

namespace MainApp.Controllers
{
    [ApiController]
    [Route("health")]
    public class HealthController : ControllerBase
    {
        [HttpGet("check")]
        public IActionResult Check()
        {
            return Ok("App is running...");
        }
    }
}
