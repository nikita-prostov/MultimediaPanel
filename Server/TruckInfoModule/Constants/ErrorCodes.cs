using System;
using System.Collections.Generic;
using System.Text;

namespace TransportInfoModule.Constants
{
    public static class ErrorCodes
    {
        // ========== ДВИГАТЕЛЬ (Engine) ==========
        // SPN 110 - Температура охлаждающей жидкости двигателя
        public const string EngineCoolantTemperatureWarning = "SPN-110-FMI-15"; // Повышенная температура

        // SPN 100 - Давление масла двигателя
        public const string EngineOilPressureLow = "SPN-100-FMI-1";  // Давление ниже нормы

        // Общее состояние двигателя
        public const string EngineWarning = "SPN-190-FMI-15";   // Предупреждение
        public const string EngineCritical = "SPN-190-FMI-0";   // Критическая неисправность

        // ========== ТРАНСМИССИЯ (Transmission) ==========
        public const string TransmissionWarning = "SPN-191-FMI-15";
        public const string TransmissionCritical = "SPN-191-FMI-0";

        // ========== ТОПЛИВНАЯ СИСТЕМА (Fuel) ==========
        // SPN 96 - Уровень топлива
        public const string FuelLow = "SPN-96-FMI-1";  // Низкий уровень

        // SPN 1761 - Уровень AdBlue/DEF
        public const string AdBlueLow = "SPN-1761-FMI-1";

        // ========== ЭЛЕКТРИКА (Electrical) ==========
        // SPN 168 - Напряжение АКБ
        public const string BatteryVoltageLow = "SPN-168-FMI-1";

        // ========== ПНЕВМОСИСТЕМА (Air) ==========
        // SPN 109 - Давление воздуха в контуре 1
        public const string AirPressureLow = "SPN-109-FMI-1";
        public const string AirPressureEmergency = "SPN-109-FMI-0";  // Аварийно низкое

        // ========== FMI (Failure Mode Indicator) ==========
        // FMI 0 - Данные выше нормы (критически высокое)
        // FMI 1 - Данные ниже нормы (критически низкое)
        // FMI 15 - Данные выше нормы, но не критично (предупреждение)
        // FMI 17 - Данные ниже нормы, но не критично (предупреждение)

        public static string GetSeverity(string code)
        {
            if (code.Contains("FMI-0") || code.Contains("FMI-1"))
                return "ERROR";
            if (code.Contains("FMI-15") || code.Contains("FMI-17"))
                return "WARNING";
            return "UNKNOWN";
        }

        public static string GetDescription(string code) => code switch
        {
            EngineCoolantTemperatureWarning => "Повышенная температура двигателя: температура охлаждающей жидкости > 100°C",
            EngineOilPressureLow => "Давление масла ниже критической отметки",
            EngineWarning => "Неисправность двигателя: износ 10-30%",
            EngineCritical => "Критическая неисправность двигателя: износ > 30%",
            TransmissionWarning => "Неисправность трансмиссии: износ 10-30%",
            TransmissionCritical => "Критическая неисправность трансмиссии: износ > 30%",
            FuelLow => "Низкий уровень топлива: остаток < 10% бака",
            AdBlueLow => "Низкий уровень реагента AdBlue: остаток < 10 л",
            BatteryVoltageLow => "Напряжение АКБ ниже нормы: < 12V",
            AirPressureLow => "Давление воздуха в пневмосистеме ниже нормы: < 80 psi",
            AirPressureEmergency => "Аварийно низкое давление воздуха: < 40 psi",
            _ => "Неизвестный код ошибки"
        };

        // SPN (Suspect Parameter Number) — номер параметра
        public static string GetSpn(string code) => code.Split('-')[1];

        // FMI (Failure Mode Indicator) — тип неисправности
        public static string GetFmi(string code) => code.Split('-')[3];
    }
}
