using System;
using System.Collections.Generic;
using System.Text;

namespace CommonDataModule.Models
{
    public class CommonData
    {
        public TimeOnly NextRestStopAfter { get; set; }
        public DateTime NextRestStopTime { get; set; }
        public DateTime CurrentGameTime { get; set; }
    }
}
