using System;
using System.Collections.Generic;
using System.Text;

namespace TransportInfoModule.Constants
{
    public static class ErrorCodes
    {
        // ========== ДВИГАТЕЛЬ (Engine) ==========
        // SPN 110 - Температура охлаждающей жидкости двигателя
        public const string EngineCoolantTemperatureHigh = "SPN-110-FMI-0";  // Критический перегрев
        public const string EngineCoolantTemperatureWarning = "SPN-110-FMI-15"; // Повышенная температура

        // SPN 100 - Давление масла двигателя
        public const string EngineOilPressureLow = "SPN-100-FMI-1";  // Давление ниже нормы

        // Общее состояние двигателя
        public const string EngineWarning = "SPN-190-FMI-15";   // Предупреждение
        public const string EngineCritical = "SPN-190-FMI-0";   // Критическая неисправность

        public const string EngineCoolantTemperatureCritical = "SPN-110-FMI-0";  // Вода > 115°C
        public const string EngineOilTemperatureWarning = "SPN-175-FMI-15";      // Масло > 120°C
        public const string EngineOilTemperatureCritical = "SPN-175-FMI-0";      // Масло > 140°C

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
        public const string BatteryVoltageHigh = "SPN-168-FMI-0";

        // ========== ПНЕВМОСИСТЕМА (Air) ==========
        // SPN 109 - Давление воздуха в контуре 1
        public const string AirPressureLow = "SPN-109-FMI-1";
        public const string AirPressureEmergency = "SPN-109-FMI-0";  // Аварийно низкое

        // ========== ТОРМОЗНАЯ СИСТЕМА (Brakes) ==========
        // SPN 108 - Давление воздуха в тормозной системе
        public const string BrakeAirPressureLow = "SPN-108-FMI-1";
        public const string BrakeAirPressureEmergency = "SPN-108-FMI-0";

        // ========== ГРУППЫ ==========
        public static readonly string[] All = [
            EngineCoolantTemperatureHigh, EngineCoolantTemperatureWarning,
            EngineCoolantTemperatureCritical,
            EngineOilPressureLow,
            EngineOilTemperatureWarning, EngineOilTemperatureCritical,
            EngineWarning, EngineCritical,
            TransmissionWarning, TransmissionCritical,
            FuelLow, AdBlueLow,
            BatteryVoltageLow, BatteryVoltageHigh,
            AirPressureLow, AirPressureEmergency,
            BrakeAirPressureLow, BrakeAirPressureEmergency
        ];

        public static readonly string[] Warnings = [
            EngineCoolantTemperatureWarning,
            EngineOilTemperatureWarning,
            EngineWarning,
            TransmissionWarning, FuelLow, AdBlueLow,
            BatteryVoltageLow, AirPressureLow, BrakeAirPressureLow
        ];

        public static readonly string[] Errors = [
            EngineCoolantTemperatureHigh, EngineCoolantTemperatureCritical,
            EngineOilPressureLow, EngineOilTemperatureCritical,
            EngineCritical, TransmissionCritical,
            BatteryVoltageHigh, AirPressureEmergency, BrakeAirPressureEmergency
        ];

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
            EngineCoolantTemperatureHigh => "Критический перегрев двигателя: температура охлаждающей жидкости > 115°C",
            EngineCoolantTemperatureWarning => "Повышенная температура двигателя: температура охлаждающей жидкости > 100°C",
            EngineOilPressureLow => "Давление масла ниже критической отметки",
            EngineOilTemperatureWarning => "Повышенная температура масла: > 120°C",
            EngineOilTemperatureCritical => "Критическая температура масла: > 140°C",
            EngineWarning => "Неисправность двигателя: износ 10-30%",
            EngineCritical => "Критическая неисправность двигателя: износ > 30%",
            TransmissionWarning => "Неисправность трансмиссии: износ 10-30%",
            TransmissionCritical => "Критическая неисправность трансмиссии: износ > 30%",
            FuelLow => "Низкий уровень топлива: остаток < 10% бака",
            AdBlueLow => "Низкий уровень реагента AdBlue: остаток < 10 л",
            BatteryVoltageLow => "Напряжение АКБ ниже нормы: < 12V",
            BatteryVoltageHigh => "Напряжение АКБ выше нормы: > 15V",
            AirPressureLow => "Давление воздуха в пневмосистеме ниже нормы: < 80 psi",
            AirPressureEmergency => "Аварийно низкое давление воздуха: < 40 psi",
            BrakeAirPressureLow => "Давление в тормозной системе ниже нормы",
            BrakeAirPressureEmergency => "Аварийно низкое давление в тормозной системе",
            _ => "Неизвестный код ошибки"
        };

        // SPN (Suspect Parameter Number) — номер параметра
        public static string GetSpn(string code) => code.Split('-')[1];

        // FMI (Failure Mode Indicator) — тип неисправности
        public static string GetFmi(string code) => code.Split('-')[3];
    }
}
