using System;
using System.Collections.Generic;
using System.Text;

namespace TransportInfoModule.Models
{
    public class ErrorDto
    {
        public string Code { get; set; }
        public DateTime DateTime { get; set; }
        public string Description { get; set; }
    }
}
