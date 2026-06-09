using Microsoft.EntityFrameworkCore;
using NotificationsModule.Data;
using NotificationsModule.Enums;
using NotificationsModule.Models;
using SCSSdkClient;
using SCSSdkClient.Object;
using System;
using System.Collections.Generic;
using System.Text;

namespace NotificationsModule.Services
{
    public sealed class NotificationService : IDisposable
    {
        private readonly NotificationDbContext dbContext;
        private readonly SCSSdkTelemetry telemetry;
        private SCSTelemetry data = new();

        public NotificationDto? LastNotification { get; set; }

        public NotificationService(NotificationDbContext dbContext, SCSSdkTelemetry telemetry)
        {
            this.dbContext = dbContext;
            this.telemetry = telemetry;
            this.telemetry.Data += OnDataChanged;
        }

        public async Task<List<NotificationDto>> GetListAsync(int page)
        {
            return await dbContext.Notifications
                .OrderByDescending(n => n.DateTime)
                .Skip((page - 1) * 100)
                .Take(100)
                .Select(n => new NotificationDto
                    {
                        Title = n.Title,
                        SubTitle = n.SubTitle,
                        Amount = n.Amount,
                        Type = n.Type,
                        DateTime = n.DateTime,
                    }
                )
                .AsNoTracking()
                .ToListAsync();
        }

        public async Task<List<NotificationDto>> GetFilteredListAsync(int page,DateTime? from = null, DateTime? to = null,NotificationType? type = null, string? title = null)
        {
            from ??= DateTime.MinValue;
            to ??= data.CommonValues.GameTime.Date;
            type ??= NotificationType.None;

            return await dbContext.Notifications
                .OrderByDescending(n => n.DateTime)
                .Where(n => n.DateTime >= from && n.DateTime <= to)
                .Where(n => type == NotificationType.None || n.Type == type)
                .Where(n => string.IsNullOrEmpty(title) || n.Title.Contains(title))
                .Skip((page - 1) * 100)
                .Take(100)
                .Select(n => new NotificationDto
                {
                    Title = n.Title,
                    SubTitle = n.SubTitle,
                    Amount = n.Amount,
                    Type = n.Type,
                    DateTime = n.DateTime,
                })
                .AsNoTracking()
                .ToListAsync();
        }

        public void Dispose()
        {
            telemetry.Data -= OnDataChanged;
        }
        private void OnTollgate()
        {
            LastNotification = new()
            {
                Title = "Пункт оплаты",
                SubTitle = "Проезд оплачен",
                Amount = data.GamePlay.TollgateEvent.PayAmount,
                Type = NotificationType.Tollgate,
                DateTime = data.CommonValues.GameTime.Date
            };
            Save();
        }

        private void OnFerry()
        {
            LastNotification = new()
            {
                Title = "Паром",
                SubTitle = "Переправа из: " + data.GamePlay.FerryEvent.SourceName + " в: " + data.GamePlay.FerryEvent.TargetName,
                Amount = data.GamePlay.FerryEvent.PayAmount,
                Type = NotificationType.Ferry,
                DateTime = data.CommonValues.GameTime.Date
            };
            Save();
        }

        private void OnTrain()
        {
            LastNotification = new()
            {
                Title = "Поезд",
                SubTitle = "Переезд из: " + data.GamePlay.TrainEvent.SourceName + " в: " + data.GamePlay.TrainEvent.TargetName,
                Amount = data.GamePlay.TrainEvent.PayAmount,
                Type = NotificationType.Train,
                DateTime = data.CommonValues.GameTime.Date
            };
            Save();
        }

        private void OnFined()
        {
            LastNotification = new()
            {
                Title = "Нарушение",
                SubTitle = GetOffenceDescription(data.GamePlay.FinedEvent.Offence),
                Amount = data.GamePlay.FinedEvent.Amount,
                Type = NotificationType.Fined,
                DateTime = data.CommonValues.GameTime.Date
            };
            Save();
        }

        private static string GetOffenceDescription(Offence offence) => offence switch
            {
                Offence.Crash => "Столкновение с ТС",
                Offence.Avoid_sleeping => "Нарушение режима труда и отдыха",
                Offence.Wrong_way => "Выезд на встречную полосу",
                Offence.Speeding or Offence.Speeding_camera => "Превышение скоростного режима",
                Offence.No_lights => "Движение без включённых фар",
                Offence.Red_signal => "Проезд на красный сигнал светофора",
                Offence.Avoid_weighting => "Уклонение от весового контроля",
                Offence.Illegal_trailer => "Несоответствие прицепа требованиям региона",
                Offence.Avoid_Inspection => "Уклонение от пограничной проверки",
                Offence.Illegal_Border_Crossing => "Нарушение правил пересечения границы",
                Offence.Hard_Shoulder_Violation => "Движение по обочине",
                Offence.Damaged_Vehicle_Usage => "Эксплуатация неисправного ТС",
                _ => "Нарушение не классифицировано",
            };

        private void OnDataChanged(SCSTelemetry data, bool newTimestamp)
        {
            this.data = data;
            if (data.SpecialEventsValues.Tollgate) OnTollgate();
            if (data.SpecialEventsValues.Ferry) OnFerry();
            if (data.SpecialEventsValues.Train) OnTrain();
            if (data.SpecialEventsValues.Fined) OnFined();
            else LastNotification = null;
        }

        private void Save()
        {
            if(LastNotification == null) return;
            var notification = new Notification
            {
                Title = LastNotification.Title,
                SubTitle = LastNotification.SubTitle,
                Amount = LastNotification.Amount,
                Type = LastNotification.Type,
                DateTime = LastNotification.DateTime,
            };
            dbContext.Notifications.Add(notification);
            dbContext.SaveChanges();
        }
    }
}
