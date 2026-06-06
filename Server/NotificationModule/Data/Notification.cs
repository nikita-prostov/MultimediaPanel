using NotificationsModule.Enums;
using System;
using System.Collections.Generic;
using System.Text;

namespace NotificationsModule.Data
{
    internal class Notification
    {
        public long Id { get; set; }
        public string Title { get; set; }
        public string? SubTitle { get; set; }
        public NotificationType Type { get; set; }
        public long Amount { get; set; }
        public DateTime DateTime { get; set; }
    }
}
