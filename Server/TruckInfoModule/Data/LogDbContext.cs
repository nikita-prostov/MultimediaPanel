using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Text;

namespace TransportInfoModule.Data
{
    public class LogDbContext(DbContextOptions<LogDbContext> options) : DbContext(options)
    {
        public DbSet<Log> Logs { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Log>(e =>
            {
                e.HasKey(e => e.Id);
            });
            base.OnModelCreating(modelBuilder);
        }
    }
}
