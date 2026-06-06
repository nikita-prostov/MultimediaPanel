using System;
using System.Collections.Generic;
using System.Net.WebSockets;
using System.Text;
using System.Text.Json;

namespace Utils
{
    public static class WebSocketExtensions
    {
        public static async Task SendJsonAsync<T>(this WebSocket ws, T body, CancellationToken ct = default)
        {
            var json = JsonSerializer.Serialize(body);
            var bytes = Encoding.UTF8.GetBytes(json);
            await ws.SendAsync(bytes, WebSocketMessageType.Text, true, ct);
        }
    }
}
