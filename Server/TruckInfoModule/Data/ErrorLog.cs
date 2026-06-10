using System;
using System.Collections.Generic;
using System.Text;

namespace TransportInfoModule.Data
{
    public class Log
    {
        public long Id { get; set; }
        public string LicensePlate { get; set; }
        public string Code { get; set; }
        public bool IsActive { get; set; }
        public string Description { get; set; }
        public DateTime ActiveDateTime { get; set; } 
        public DateTime? InactiveDateTime { get; set; }
    }
}
