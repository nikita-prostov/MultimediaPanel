using System;
using System.Collections.Generic;
using System.Text;

namespace JobModule.Models
{
    public class CurrentJobInfo
    {
        public DateTime DeliveryTime { get; set; }
        public uint PlanedDistance { get; set; }
        public bool IsLate { get; set; }
        public bool IsLoaded { get; set; }
        public DateTime RemainingDeliveryTime { get; set; }
        public ulong Income { get; set; }

        public Point Source {  get; set; }
        public Point Destination { get; set; }
        public Cargo Cargo { get; set; }
    }
}
