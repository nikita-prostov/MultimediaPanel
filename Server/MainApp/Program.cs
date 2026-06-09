using JobModule.Data;
using JobModule.Services;
using Microsoft.EntityFrameworkCore;
using MusicModule.Enums;
using MusicModule.Loader;
using MusicModule.Models;
using MusicModule.Services;
using NotificationsModule.Data;
using NotificationsModule.Services;
using SCSSdkClient;
using System.Web;
using VkNet;
using VkNet.Enums.Filters;
using VkNet.Model;

string path = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "MultimediaPanel");
string cachePath = Path.Combine(Path.GetTempPath(), "MultimediaPanel");
VkApi? api = null;
TracksSource source = TracksSource.MyMusic;

start:
Console.Clear();
Console.WriteLine("Change track source:");
Console.WriteLine("1) My music");
Console.WriteLine("2) Recomendations");
Console.WriteLine("3) Local - don't work");
var key = Console.ReadKey(true);
var tracks = new List<AudioTrack>();
string accessToken = "";
long userId;
try
{
    source = (TracksSource)int.Parse(key.KeyChar.ToString())-1;
    if (source == TracksSource.MyMusic || source == TracksSource.Recomendations)
    {
        
        Console.WriteLine("Authorization instruction:");
        Console.WriteLine("Open link: ");
        Console.ForegroundColor = ConsoleColor.Blue;
        Console.WriteLine("https://oauth.vk.com/authorize?client_id=6287487&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=audio,offline&response_type=token&v=5.131");
        Console.ResetColor();
        Console.WriteLine("after confirmation, copy your new URL:");
        string url = Console.ReadLine();
        (string? t, long? u) = Parse(url);
        accessToken = t ?? string.Empty;
        userId = u ?? 0L;
        api = await AuthorizeApp(userId, accessToken);

        tracks = await TrackLoader.LoadAsync(source, api);
    }
    else if (source == TracksSource.Local)
    {
        Console.WriteLine("This source is not yet available.");
        await Task.Delay(3000);
        goto start;
    }
}
catch (Exception ex)
{
    Console.WriteLine("Exception: " + ex.ToString());
    Console.WriteLine("Message: " + ex.Message.ToString());
    Console.WriteLine("StackTrace: " + ex.StackTrace);
    Console.WriteLine("InnerException: " + ex.InnerException?.Message.ToString());
}



var builder = WebApplication.CreateBuilder(args);

var musicService = new MusicService(path, cachePath, 0.1f, tracks,api,source,accessToken);
builder.Services.AddSingleton(musicService);
builder.Services.AddSingleton<SCSSdkTelemetry>();

var notificationDbFilePath = Path.Combine(path, "notification.db");
builder.Services.AddDbContext<NotificationDbContext>(options => options.UseSqlite(notificationDbFilePath));

var jobDbFilePath = Path.Combine(path, "jobHistory.db");
builder.Services.AddDbContext<JobDbContext>(options => options.UseSqlite(jobDbFilePath));

builder.Services.AddSingleton<NotificationService>();
builder.Services.AddSingleton<JobService>();

builder.Services.AddControllers();

var app = builder.Build();

app.UseAuthorization();
app.UseWebSockets();
app.MapControllers();

app.Run();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<NotificationDbContext>();
    await db.Database.MigrateAsync();
}

static (string? accessToken, long? userId) Parse(string url)
{
    if (string.IsNullOrEmpty(url))
        return (null, null);

    try
    {
        int hashIndex = url.IndexOf('#');
        if (hashIndex == -1)
            return (null, null);

        string fragment = url[(hashIndex + 1)..];

        var parameters = HttpUtility.ParseQueryString(fragment);

        string? accessToken = parameters["access_token"];
        string? userIdStr = parameters["user_id"];

        long? userId = long.TryParse(userIdStr, out long id) ? id : null;

        return (accessToken, userId);
    }
    catch
    {
        return (null, null);
    }
}

static async Task<VkApi> AuthorizeApp(long userId,string accessToken)
{
    var api = new VkApi();
    await api.AuthorizeAsync(new ApiAuthParams
    {
        UserId = userId,
        AccessToken = accessToken,
        Settings = Settings.Audio
    });
    return api;
}
