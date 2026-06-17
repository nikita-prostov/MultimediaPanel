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
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace TransportInfoModule.Services
{
    public class TransportInfoService
    {
        private readonly SCSSdkTelemetry telemetry;
        private readonly IServiceScopeFactory scopeFactory;
        private bool engineStarted = false;
        private DateTime? engineStartTime = null;

        public TransportInfo? TransportInfo { get; private set; } = null;

        public string LicensePlate;

        public TransportInfoService(SCSSdkTelemetry telemetry, IServiceScopeFactory scopeFactory) 
        {
            this.telemetry = telemetry;
            this.scopeFactory = scopeFactory;
            this.telemetry.Data += OnDataChanged;
        }

        public async Task ClearLogsAsync()
        {
            using var scope = scopeFactory.CreateScope();
            var dbContext = scope.ServiceProvider.GetRequiredService<LogDbContext>();
            await dbContext.Logs.Where(l => !l.IsActive).ExecuteDeleteAsync();
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

            query = query.OrderBy(l => l.IsActive);
            query = query.OrderBy(l => l.ActiveDateTime);

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

            TransportInfo = new();
            WriteFuelInfo(data.TruckValues);
            WriteTransportDamage(data.TruckValues.CurrentValues, data.TrailerValues);
            

            if (data.TruckValues.CurrentValues.DashboardValues.RPM > 500)
            {
                if (!engineStarted)
                {
                    engineStarted = true;
                    engineStartTime = data.CommonValues.GameTime.Date;
                }

                // Проверяем ошибки только через 3 секунды после запуска
                if (engineStartTime.HasValue && (data.CommonValues.GameTime.Date - engineStartTime.Value).TotalSeconds > 3)
                {
                    WriteActiveErrors(
                        data.TruckValues.CurrentValues,
                        data.TruckValues.ConstantsValues,
                        data.CommonValues.GameTime.Date
                    );

                    try
                    {
                        UpdateLogs(data.CommonValues.GameTime.Date);
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine(ex.Message);
                    }
                }
            }
            else
            {
                TransportInfo = null;
            }
        }

#pragma warning disable
        private void WriteFuelInfo(SCSTelemetry.Truck truck)
        {
            var fuelInfo = truck.CurrentValues.DashboardValues.FuelValue;
            TransportInfo.FuelInfo.Current = fuelInfo.Amount;
            TransportInfo.FuelInfo.Max = truck.ConstantsValues.CapacityValues.Fuel;
            TransportInfo.FuelInfo.AverageConsumption = fuelInfo.AverageConsumption * 100;
            TransportInfo.FuelInfo.AdBlueLevel = truck.CurrentValues.DashboardValues.AdBlue;
            TransportInfo.FuelInfo.AdBlueMax = truck.ConstantsValues.CapacityValues.AdBlue;
            TransportInfo.FuelInfo.Range = fuelInfo.Range;
        }

        private void WriteTransportDamage(SCSTelemetry.Truck.Current truck, SCSTelemetry.Trailer[] trailers)
        {
            var truckDamage = truck.DamageValues;
            var warnings = truck.DashboardValues.WarningValues;

            TransportInfo.TruckDamage.Cabin = (int)(truckDamage.Cabin * 100);
            TransportInfo.TruckDamage.Chassis = (int)(truckDamage.Chassis * 100);
            TransportInfo.TruckDamage.Transmission = (int)(truckDamage.Transmission * 100);
            TransportInfo.TruckDamage.Engine = (int)(truckDamage.Engine * 100);
            TransportInfo.TruckDamage.WheelsAvg = (int)(truckDamage.WheelsAvg * 100);
            TransportInfo.TruckDamage.Average = (int)(((truckDamage.Cabin + truckDamage.Chassis + truckDamage.Transmission + truckDamage.Engine + truckDamage.WheelsAvg) / 5) * 100);

            if (trailers.Length > 0)
            {
                var trailerDamage = trailers[0].DamageValues;
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
        }

        private void WriteActiveErrors(SCSTelemetry.Truck.Current current, SCSTelemetry.Truck.Constants constants, DateTime dateTime)
        {
            TransportInfo.Errors.Clear();

            var dashboard = current.DashboardValues;
            var damage = current.DamageValues;

            if (dashboard.WarningValues.AirPressure)
            {
                TransportInfo.Errors.Add(new()
                {
                    Code = ErrorCodes.AirPressureLow,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.AirPressureLow)
                });
            }

            if (dashboard.WarningValues.AirPressureEmergency)
            {
                TransportInfo.Errors.Add(new()
                {
                    Code = ErrorCodes.AirPressureEmergency,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.AirPressureEmergency)
                });
            }

            if (dashboard.WarningValues.FuelW)
            {
                TransportInfo.Errors.Add(new()
                {
                    Code = ErrorCodes.FuelLow,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.FuelLow)
                });
            }

            if (dashboard.WarningValues.AdBlue)
            {
                TransportInfo.Errors.Add(new()
                {
                    Code = ErrorCodes.AdBlueLow,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.AdBlueLow)
                });
            }

            if (dashboard.WarningValues.OilPressure)
            {
                TransportInfo.Errors.Add(new()
                {
                    Code = ErrorCodes.EngineOilPressureLow,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.EngineOilPressureLow)
                });
            }

            if (dashboard.WarningValues.WaterTemperature)
            {
                TransportInfo.Errors.Add(new()
                {
                    Code = ErrorCodes.EngineCoolantTemperatureWarning,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.EngineCoolantTemperatureWarning)
                });
            }

            if (dashboard.WarningValues.BatteryVoltage)
            {
                TransportInfo.Errors.Add(new()
                {
                    Code = ErrorCodes.BatteryVoltageLow,
                    DateTime = dateTime,
                    Description = ErrorCodes.GetDescription(ErrorCodes.BatteryVoltageLow)
                });
            }

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
        }
#pragma warning enable
        private async void UpdateLogs(DateTime dateTime)
        {
            using var scope = scopeFactory.CreateScope();
            var dbContext = scope.ServiceProvider.GetRequiredService<LogDbContext>();

            // Все активные ошибки из БД
            var activeErrors = await dbContext.Logs
                .Where(l => l.IsActive && l.LicensePlate == LicensePlate)
                .ToListAsync();

            // Деактивируем те, которых больше нет в текущем списке
            foreach (var activeError in activeErrors)
            {
                if (!TransportInfo.Errors.Any(e => e.Code == activeError.Code))
                {
                    activeError.IsActive = false;
                    activeError.InactiveDateTime = dateTime;
                }
            }

            // Добавляем новые ошибки (проверяем и в activeErrors, и в только что добавленных)
            var newlyAddedCodes = new HashSet<string>();  // ← Запоминаем, что уже добавили

            foreach (var error in TransportInfo.Errors)
            {
                // Проверяем, есть ли ошибка в БД (activeErrors) ИЛИ уже добавлена в этом кадре
                if (activeErrors.Any(e => e.Code == error.Code) || newlyAddedCodes.Contains(error.Code))
                    continue;  // ← Уже есть — пропускаем

                var newLog = new Log
                {
                    LicensePlate = LicensePlate,
                    Code = error.Code,
                    ActiveDateTime = error.DateTime,
                    IsActive = true,
                    Description = error.Description
                };
                dbContext.Logs.Add(newLog);
                newlyAddedCodes.Add(error.Code);  // ← Запоминаем
            }

            await dbContext.SaveChangesAsync();
        }
    }
}
