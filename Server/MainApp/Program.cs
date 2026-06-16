using CommonDataModule.Services;
using JobModule.Data;
using JobModule.Services;
using Microsoft.EntityFrameworkCore;
using MusicModule.Data;
using MusicModule.Enums;
using MusicModule.Loader;
using MusicModule.Models;
using MusicModule.Services;
using NotificationsModule.Data;
using NotificationsModule.Services;
using SCSSdkClient;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Web;
using TransportInfoModule.Services;
using VkNet;
using VkNet.Enums.Filters;
using VkNet.Model;

string path = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "MultimediaPanel");
string cachePath = Path.Combine(Path.GetTempPath(), "MultimediaPanel");

if(!Directory.Exists(path)) Directory.CreateDirectory(path);
if(!Directory.Exists(cachePath)) Directory.CreateDirectory(cachePath);

VkApi? api = null;
TracksSource source = TracksSource.MyMusic;

start:
Console.Clear();
Console.WriteLine("Change track source:");
Console.WriteLine("1) My music");
Console.WriteLine("2) Local cache");
var key = Console.ReadKey(true);
var tracks = new List<AudioTrack>();
string accessToken = "";
long userId;
try
{
    if (key.KeyChar == '2')
    {
        source = TracksSource.Local;
        var tempOptions = new DbContextOptionsBuilder<MusicDbContext>()
            .UseSqlite("Data Source=" + Path.Combine(path, "musicDb.db"))
            .Options;
        using var tempDb = new MusicDbContext(tempOptions);
        tracks = await TrackLoader.LoadAsync(source, dbContext: tempDb, path: path);
        Console.WriteLine($"Загружено из кэша: {tracks.Count} треков");
    }
    else if (key.KeyChar == '1')
    {
        Console.WriteLine("Authorization instruction:");
        Console.WriteLine("1) Open link: ");
        Console.ForegroundColor = ConsoleColor.Blue;
        Console.WriteLine("https://oauth.vk.com/authorize?client_id=6287487&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=audio,offline&response_type=token&v=5.131");
        Console.ResetColor();
        Console.WriteLine("2) After confirmation, copy your new URL:");
        string url = Console.ReadLine();
        (string? t, long? u) = Parse(url);
        accessToken = t ?? string.Empty;
        userId = u ?? 0L;
        api = await AuthorizeApp(userId, accessToken);

        tracks = await TrackLoader.LoadAsync(source, api,page:1);
    }
    else
    {
        Console.WriteLine("Unsupported source");
        await Task.Delay(1000);
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

var notificationDbFilePath = Path.Combine(path, "notification.db");
builder.Services.AddDbContext<NotificationDbContext>(options => options.UseSqlite("Data Source=" + notificationDbFilePath));

var jobDbFilePath = Path.Combine(path, "jobHistory.db");
builder.Services.AddDbContext<JobDbContext>(options => options.UseSqlite("Data Source=" + jobDbFilePath));

var musicDbFilePath = Path.Combine(path, "musicDb.db");
builder.Services.AddDbContext<MusicDbContext>(options => options.UseSqlite("Data Source=" + musicDbFilePath));

builder.Services.AddSingleton<SCSSdkTelemetry>();
builder.Services.AddSingleton<NotificationService>();
builder.Services.AddSingleton<JobService>();
builder.Services.AddSingleton<TransportInfoService>();
builder.Services.AddSingleton<CommonDataService>();
builder.Services.AddSingleton<MusicService>(sp =>
{
    var scopeFactory = sp.GetRequiredService<IServiceScopeFactory>();
    return new MusicService(
        scopeFactory,
        path,
        cachePath,
        0.1f,
        tracks,
        api,
        source,
        accessToken
    );
});

builder.Services.AddScoped(s =>
{
    return new JsonSerializerOptions
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
        Converters = { new JsonStringEnumConverter() }
    };
});

builder.Services.AddControllers().AddJsonOptions(options =>
{
    options.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;
    options.JsonSerializerOptions.Converters.Add(new  JsonStringEnumConverter());
});


var app = builder.Build();

app.UseAuthorization();
app.UseWebSockets();
app.MapControllers();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<NotificationDbContext>();
    await db.Database.MigrateAsync();
    var db1 = scope.ServiceProvider.GetRequiredService<JobDbContext>();
    await db1.Database.MigrateAsync();
    var db2 = scope.ServiceProvider.GetRequiredService<MusicDbContext>();
    await db2.Database.MigrateAsync();
}

app.Run("http://0.0.0.0:5110");

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
