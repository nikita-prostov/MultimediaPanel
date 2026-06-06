using System.Text.Json.Serialization;

namespace MainApp.Models
{
    public class ClientCommand
    {
        [JsonPropertyName("command")]
        public string Command { get; set; }

        [JsonPropertyName("parameters")]
        public Dictionary<string, string>? Parameters { get; set; } = null;
    }
}
