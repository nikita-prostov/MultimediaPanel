using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using SCSSdkClient;
using SCSSdkClient.Object;
using System;
using System.Collections.Generic;
using System.Text;
using TransportInfoModule.Constants;
using TransportInfoModule.Data;
using TransportInfoModule.Models;

namespace TransportInfoModule.Services
{
    public class TransportInfoService
    {
        private readonly SCSSdkTelemetry telemetry;
        private readonly IServiceScopeFactory scopeFactory;

        public TransportInfo TransportInfo { get; private set; } = new();

        public string LicensePlate;

        public TransportInfoService(SCSSdkTelemetry telemetry, IServiceScopeFactory scopeFactory) 
        {
            this.telemetry = telemetry;
            this.scopeFactory = scopeFactory;
            this.telemetry.Data += OnDataChanged;
        }

        public async Task<List<FullLogDto>> GetFullLogs(DateTime? from = null, DateTime? to = null, bool activeOnly = false)
        {
            using var scope = scopeFactory.CreateScope();
            var dbContext = scope.ServiceProvider.GetRequiredService<LogDbContext>();

            IQueryable<Log> query = dbContext.Logs;

            if (from.HasValue)
                query = query.Where(l => l.ActiveDateTime >= from.Value);

            if (to.HasValue)
                query = query.Where(l => l.ActiveDateTime <= to.Value);

            if (activeOnly)
                query = query.Where(l => l.IsActive);

            var logs = await query.ToListAsync();

            return [.. logs.Select(l => new FullLogDto
            {
                Code = l.Code,
                IsActive = l.IsActive,
                Description = l.Description,
                ActiveDateTime = l.ActiveDateTime,
                InactiveDateTime = l.InactiveDateTime
            })];
        }

        private void OnDataChanged(SCSTelemetry data, bool newTimestamp)
        {
            LicensePlate = data.TruckValues.ConstantsValues.LicensePlate;

            var fuelInfo = data.TruckValues.CurrentValues.DashboardValues.FuelValue;
            TransportInfo.FuelInfo.Current = fuelInfo.Amount;
            TransportInfo.FuelInfo.Max = data.TruckValues.ConstantsValues.CapacityValues.Fuel;
            TransportInfo.FuelInfo.AverageConsumption = fuelInfo.AverageConsumption * 100;
            TransportInfo.FuelInfo.Range = fuelInfo.Range;

            var truckDamage = data.TruckValues.CurrentValues.DamageValues;
            var warnings = data.TruckValues.CurrentValues.DashboardValues.WarningValues;

            TransportInfo.TruckDamage.Cabin = (int)(truckDamage.Cabin * 100);
            TransportInfo.TruckDamage.Chassis = (int)(truckDamage.Chassis * 100);
            TransportInfo.TruckDamage.Transmission = (int)(truckDamage.Transmission * 100);
            TransportInfo.TruckDamage.Engine = (int)(truckDamage.Engine * 100);
            TransportInfo.TruckDamage.WheelsAvg = (int)(truckDamage.WheelsAvg * 100);
            TransportInfo.TruckDamage.Average = (int)(((truckDamage.Cabin + truckDamage.Chassis + truckDamage.Transmission + truckDamage.Engine + truckDamage.WheelsAvg) / 5) * 100);

            if(data.TrailerValues.Length > 0)
            {
                var trailerDamage = data.TrailerValues[0].DamageValues;
                TransportInfo.TrailerDamage = new()
                {
                    Body = (int)(trailerDamage.Body * 100),
                    Chassis = (int)(trailerDamage.Chassis * 100),
                    Wheels = (int)(trailerDamage.Wheels * 100),
                    Average = (int)(((trailerDamage.Chassis + trailerDamage.Body + trailerDamage.Wheels) / 3) * 100)
                };
            }
            else
            {
                TransportInfo.TrailerDamage = null;
            }
            WriteActiveErrors(
                data.TruckValues.CurrentValues,
                data.TruckValues.ConstantsValues,
                data.CommonValues.GameTime.Date
                );
        }

        private void WriteActiveErrors(SCSTelemetry.Truck.Current current, SCSTelemetry.Truck.Constants constants, DateTime dateTime)
        {
            TransportInfo.Errors.Clear();

            var dashboard = current.DashboardValues;
            var damage = current.DamageValues;
            var fuel = dashboard.FuelValue;

            // Топливо — меньше 10% от максимума? (макс можно взять из констант)
            if (fuel.Amount < constants.WarningFactorValues.Fuel)  // Например, меньше 50 литров
                TransportInfo.Errors.Add(new() { 
                    Code = ErrorCodes.FuelLow, 
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.FuelLow)
                });

            // AdBlue — меньше 10 литров?
            if (dashboard.AdBlue < constants.WarningFactorValues.AdBlue)
                TransportInfo.Errors.Add(new() { 
                    Code = ErrorCodes.AdBlueLow,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.AdBlueLow)
                });

            // Давление воздуха — меньше 8 bar? (или psi из SDK)
            if (current.MotorValues.BrakeValues.AirPressure < constants.WarningFactorValues.AirPressure)  // psi
                TransportInfo.Errors.Add(new() { 
                    Code = ErrorCodes.AirPressureLow,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.AirPressureLow)
                });

            if (current.MotorValues.BrakeValues.AirPressure < 30f || current.DashboardValues.WarningValues.AirPressureEmergency)  // Критическое
                TransportInfo.Errors.Add(new() { 
                    Code = ErrorCodes.AirPressureEmergency,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.AirPressureEmergency)
                });

            // Давление масла — меньше 10 psi?
            if (dashboard.OilPressure < constants.WarningFactorValues.OilPressure)
                TransportInfo.Errors.Add(new() {
                    Code = ErrorCodes.EngineOilPressureLow,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.EngineOilPressureLow)
                });

            // Температура воды
            if (dashboard.WaterTemperature > (constants.WarningFactorValues.WaterTemperature + 15))
                TransportInfo.Errors.Add(new() {
                    Code = ErrorCodes.EngineCoolantTemperatureCritical,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.EngineCoolantTemperatureCritical)
                });
            else if (dashboard.WaterTemperature > constants.WarningFactorValues.WaterTemperature)
                TransportInfo.Errors.Add(new() { 
                    Code = ErrorCodes.EngineCoolantTemperatureWarning,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.EngineCoolantTemperatureWarning)
                });

            // Температура масла
            if (dashboard.OilTemperature > 140f)
                TransportInfo.Errors.Add(new() { 
                    Code = ErrorCodes.EngineOilTemperatureCritical,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.EngineOilTemperatureCritical)
                });
            else if (dashboard.OilTemperature > 120f)
                TransportInfo.Errors.Add(new() { 
                    Code = ErrorCodes.EngineOilTemperatureWarning, 
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.EngineOilTemperatureWarning)
                });

            // Напряжение АКБ — ниже 12V?
            if (dashboard.BatteryVoltage < constants.WarningFactorValues.BatteryVoltage)
                TransportInfo.Errors.Add(new() {
                    Code = ErrorCodes.BatteryVoltageLow,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.BatteryVoltageLow)
                });
            else if (dashboard.BatteryVoltage > 15)
                TransportInfo.Errors.Add(new()
                {
                    Code = ErrorCodes.BatteryVoltageHigh,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.BatteryVoltageHigh)
                });

            // Повреждения
            if (damage.Engine > 0.3f)
                TransportInfo.Errors.Add(new() { 
                    Code = ErrorCodes.EngineCritical,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.EngineCritical)
                });
            else if (damage.Engine >= 0.1f)
                TransportInfo.Errors.Add(new() { 
                    Code = ErrorCodes.EngineWarning, 
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.EngineWarning)
                });

            if (damage.Transmission > 0.3f)
                TransportInfo.Errors.Add(new() {
                    Code = ErrorCodes.TransmissionCritical,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.TransmissionCritical)
                });
            else if (damage.Transmission >= 0.1f)
                TransportInfo.Errors.Add(new() {
                    Code = ErrorCodes.TransmissionWarning,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.TransmissionWarning)
                });

            try
            {
                UpdateLogs(dateTime);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message.ToString());
                if(ex.InnerException != null)
                {
                    Console.WriteLine(ex.InnerException.Message.ToString());
                }
            }
        }

        private async void UpdateLogs(DateTime dateTime)
        {
            using var scope = scopeFactory.CreateScope();
            var dbContext = scope.ServiceProvider.GetRequiredService<LogDbContext>();

            var activeErrors = await dbContext.Logs
                .Where(l => l.IsActive && l.LicensePlate == LicensePlate)
                .ToListAsync();

            foreach (var activeError in activeErrors)
            {
                if (!TransportInfo.Errors.Any(e => e.Code == activeError.Code))
                {
                    activeError.IsActive = false;
                    activeError.InactiveDateTime = dateTime;
                }
            }

            foreach (var error in TransportInfo.Errors)
            {
                var existingError = activeErrors.FirstOrDefault(e => e.Code == error.Code);

                if (existingError == null)
                {
                    dbContext.Logs.Add(new Log
                    {
                        LicensePlate = LicensePlate,
                        Code = error.Code,
                        ActiveDateTime = error.DateTime,
                        IsActive = true,
                        Description = error.Description
                    });
                }
            }

            await dbContext.SaveChangesAsync();
        }
    }
}
