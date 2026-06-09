using System;
using System.Collections.Generic;
using System.Text;

namespace TransportInfoModule.Models
{
    public class TrailerDamage
    {
        public int Body { get; set; }
        public int Chassis { get; set; }
        public int Wheels { get; set; }
        public int Average { get; set; }
    }
}
