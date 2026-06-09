using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Text;

namespace MusicModule.Data
{
    public class MusicDbContext(DbContextOptions<MusicDbContext> options) : DbContext(options)
    {
        public DbSet<Music> Musics { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Music>(e =>
            {
                e.HasKey(e => e.Id);
            });
            base.OnModelCreating(modelBuilder);
        }
    }
}
