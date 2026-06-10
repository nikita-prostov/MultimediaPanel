using System;
using System.Collections.Generic;
using System.Text;
using TransportInfoModule.Models;

namespace TransportInfoModule.Models
{
    public class TransportInfo
    {
        public TrailerDamage? TrailerDamage { get; set; } = null;
        public TruckDamage TruckDamage { get; set; } = new();
        public FuelInfo FuelInfo { get; set; } = new();
        public List<ErrorDto> Errors { get; set; } = new();
    }
}
