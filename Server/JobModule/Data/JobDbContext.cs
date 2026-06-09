using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Text;

namespace JobModule.Data
{
    public class JobDbContext(DbContextOptions<JobDbContext> options) : DbContext(options)
    {
        internal DbSet<JobInfo> Jobs { get; set; }
        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<JobInfo>(e =>
            {
                e.HasKey(e => e.Id);
            });
            base.OnModelCreating(modelBuilder);
        }
    }
}
