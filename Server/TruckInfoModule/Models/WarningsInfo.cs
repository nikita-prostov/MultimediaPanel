using System;
using System.Collections.Generic;
using System.Text;

namespace TransportInfoModule.Models
{
    public class WarningsInfo
    {
        public bool AirPressure { get; set; }
        public bool AirPressureEmergency { get; set; }
        public bool Fuel { get; set; }
        public bool AdBlue { get; set; }
        public bool OilPressure { get; set; }
        public bool WaterTemperature { get; set; }
        public bool BatteryVoltage { get; set; }
    }
}
