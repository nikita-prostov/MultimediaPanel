using SCSSdkClient;
using SCSSdkClient.Object;
using System;
using System.Collections.Generic;
using System.Text;
using TransportInfoModule.Models;

namespace TransportInfoModule.Services
{
    public class TransportInfoService
    {
        private readonly SCSSdkTelemetry telemetry;

        public TransportInfo TransportInfo { get; private set; } = new();

        public TransportInfoService(SCSSdkTelemetry telemetry) 
        {
            this.telemetry = telemetry;
            this.telemetry.Data += OnDataChanged;
        }

        private void OnDataChanged(SCSTelemetry data, bool newTimestamp)
        {
            var fuelInfo = data.TruckValues.CurrentValues.DashboardValues.FuelValue;
            TransportInfo.FuelInfo.Current = fuelInfo.Amount;
            TransportInfo.FuelInfo.Max = data.TruckValues.ConstantsValues.CapacityValues.Fuel;
            TransportInfo.FuelInfo.AverageConsumption = fuelInfo.AverageConsumption * 100;
            TransportInfo.FuelInfo.Range = fuelInfo.Range;

            var warnings = data.TruckValues.CurrentValues.DashboardValues.WarningValues;
            TransportInfo.Warnings.Fuel = warnings.FuelW;
            TransportInfo.Warnings.AirPressure = warnings.AirPressure;
            TransportInfo.Warnings.AirPressureEmergency = warnings.AirPressureEmergency;
            TransportInfo.Warnings.OilPressure = warnings.OilPressure;
            TransportInfo.Warnings.AdBlue = warnings.AdBlue;
            TransportInfo.Warnings.BatteryVoltage = warnings.BatteryVoltage;

            var truckDamage = data.TruckValues.CurrentValues.DamageValues;
            TransportInfo.TruckDamage.Cabin = (int)(truckDamage.Cabin * 100);
            TransportInfo.TruckDamage.Chassis = (int)(truckDamage.Chassis * 100);
            TransportInfo.TruckDamage.Transmission = (int)(truckDamage.Transmission * 100);
            TransportInfo.TruckDamage.Engine = (int)(truckDamage.Engine * 100);
            TransportInfo.TruckDamage.WheelsAvg = (int)(truckDamage.WheelsAvg * 100);
            TransportInfo.TruckDamage.Average = (int)(((truckDamage.Cabin + truckDamage.Chassis + truckDamage.Transmission + truckDamage.Engine + truckDamage.WheelsAvg) / 5) * 100);

            var trailerDamage = data.TrailerValues[0].DamageValues;
            TransportInfo.TrailerDamage.Body = (int)(trailerDamage.Body * 100);
            TransportInfo.TrailerDamage.Chassis = (int)(trailerDamage.Chassis * 100);
            TransportInfo.TrailerDamage.Wheels = (int)(trailerDamage.Wheels * 100);
            TransportInfo.TrailerDamage.Average = (int)(((trailerDamage.Chassis + trailerDamage.Body + trailerDamage.Wheels) / 3) * 100);
        }
    }
}
