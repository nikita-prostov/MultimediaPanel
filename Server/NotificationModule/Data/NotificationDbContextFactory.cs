using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;
using System;
using System.Collections.Generic;
using System.Text;

namespace NotificationsModule.Data
{
    public class NotificationDbContextFactory : IDesignTimeDbContextFactory<NotificationDbContext>
    {
        public NotificationDbContext CreateDbContext(string[] args)
        {
            var optionsBuilder = new DbContextOptionsBuilder<NotificationDbContext>();

            // Строка подключения для миграций (временная, для design-time)
            optionsBuilder.UseSqlite("Data Source=notification.db");

            return new NotificationDbContext(optionsBuilder.Options);
        }
    }
}
