using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;
using System;
using System.Collections.Generic;
using System.Text;

namespace TransportInfoModule.Data
{
    public class LogDbContextFactory : IDesignTimeDbContextFactory<LogDbContext>
    {
        public LogDbContext CreateDbContext(string[] args)
        {
            var optionsBuilder = new DbContextOptionsBuilder<LogDbContext>();

            optionsBuilder.UseSqlite("Data Source=errorLogs.db");
            return new LogDbContext(optionsBuilder.Options);
        }
    }
}
