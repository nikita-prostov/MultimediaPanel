using System;
using System.Collections.Generic;
using System.Text;

namespace TransportInfoModule.Models
{
    public class TruckDamage
    {
        public int Engine { get; set; }

        public int Transmission { get; set; }

        public int Cabin { get; set; }

        public int Chassis { get; set; }

        public int WheelsAvg { get; set; }
        public int Average { get; set; }
    }
}
