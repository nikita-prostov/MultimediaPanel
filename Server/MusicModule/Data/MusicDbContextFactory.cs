using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;
using System;
using System.Collections.Generic;
using System.Text;

namespace MusicModule.Data
{
    public class MusicDbContextFactory : IDesignTimeDbContextFactory<MusicDbContext>
    {
        public MusicDbContext CreateDbContext(string[] args)
        {
            var optionsBuilder = new DbContextOptionsBuilder<MusicDbContext>();

            optionsBuilder.UseSqlite("Data Source=musicCache.db");

            return new MusicDbContext(optionsBuilder.Options);
        }
    }
}
