using System;
using System.Collections.Generic;
using System.Text;

namespace JobModule.Data
{
    public class JobInfo
    {
        public long Id { get; set; }
        public DateTime AcceptedAt { get; set; }
        public DateTime FinishedAt { get; set; }
        public float Distance { get; set; }
        public bool IsLate { get; set; }
        public ulong PlannedIncome { get; set; }
        public long Income { get; set; }
        public string Source { get; set; }
        public string Destination { get; set; }
        public string Cargo { get; set; }
        public bool IsDeliveried { get; set; }
        public long Penalty { get; set; }
    }
}
