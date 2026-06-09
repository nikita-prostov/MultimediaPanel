using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;
using System;
using System.Collections.Generic;
using System.Text;

namespace JobModule.Data
{
    public class JobDbContextFactory : IDesignTimeDbContextFactory<JobDbContext>
    {
        public JobDbContext CreateDbContext(string[] args)
        {
            var optionsBuilder = new DbContextOptionsBuilder<JobDbContext>();

            optionsBuilder.UseSqlite("Data Source=jobHistory.db");

            return new JobDbContext(optionsBuilder.Options);
        }
    }
}
