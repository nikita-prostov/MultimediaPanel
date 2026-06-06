using NotificationsModule.Enums;
using System;
using System.Collections.Generic;
using System.Text;

namespace NotificationsModule.Models
{
    public class NotificationDto
    {
        public string? Title { get; set; }
        public string? SubTitle { get; set; }
        public NotificationType Type { get; set; }
        public long Amount { get; set; }
        public DateTime DateTime { get; set; }
    }
}
