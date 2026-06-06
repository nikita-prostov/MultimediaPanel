using System;
using System.Collections.Generic;
using System.Text;

namespace Utils
{
    public static class StringExtensions
    {
        public static string SanitizeFileName(this string name)
        {
            char[] invalid = Path.GetInvalidFileNameChars();
            foreach (char c in invalid)
                name = name.Replace(c, '_');
            return name;
        }
    }
}
