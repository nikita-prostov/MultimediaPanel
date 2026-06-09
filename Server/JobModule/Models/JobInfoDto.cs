using System;
using System.Collections.Generic;
using System.Text;

namespace JobModule.Models
{
    public class JobInfoDto
    {
        public DateTime AcceptedAt { get; set; }
        public DateTime FinishedAt { get; set; }
        public float Distance { get; set; }
        public bool IsLate { get; set; }
        public ulong PlannedIncome { get; set; }
        public long Income { get; set; }
        public Point Source { get; set; }
        public Point Destination { get; set; }
        public Cargo Cargo { get; set; }
        public bool IsDeliveried { get; set; }
        public long Penalty { get; set; }
    }
}
