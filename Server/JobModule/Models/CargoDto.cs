using System;
using System.Collections.Generic;
using System.Text;

namespace JobModule.Models
{
    public class Cargo
    {
        public float Mass { get; set; }
        public string Name { get; set; }
        public float Damage { get; internal set; }
    }
}
