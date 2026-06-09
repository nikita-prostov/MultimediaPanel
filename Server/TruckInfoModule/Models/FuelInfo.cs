using System;
using System.Collections.Generic;
using System.Text;

namespace TransportInfoModule.Models
{
    public class FuelInfo
    {
        public float Max { get; set; }
        public float Current { get; set; }
        public float AverageConsumption { get; set; }
        public float Range { get; internal set; }
    }
}
