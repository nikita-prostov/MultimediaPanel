using JobModule.Data;
using JobModule.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using SCSSdkClient;
using SCSSdkClient.Object;
using System;
using System.Collections.Generic;
using System.Text;
using System.Text.Json;

namespace JobModule.Services
{
    public sealed class JobService : IDisposable
    {
        private readonly SCSSdkTelemetry telemetry;
        private readonly IServiceScopeFactory scopeFactory;
        public CurrentJobInfo? CurrentJobInfo { get; set; } = null;
        public JobService(IServiceScopeFactory scopeFactory, SCSSdkTelemetry telemetry) 
        {
            this.telemetry = telemetry;
            this.telemetry.Data += OnDataChanged;
        }

        public async Task<List<JobInfoDto>> GetAll(JobDbContext dbContext,int page = 1)
        {
            return await dbContext.Jobs
                .OrderBy(j => j.FinishedAt)
                .Skip((page - 1) * 100)
                .Take(100)
                .Select(j => new JobInfoDto
                    {
                        AcceptedAt = j.AcceptedAt,
                        FinishedAt = j.FinishedAt,
                        Distance = j.Distance,
                        IsLate = j.IsLate,
                        PlannedIncome = j.PlannedIncome,
                        Income = j.Income,
                        Source = JsonSerializer.Deserialize<Point>(j.Source),
                        Destination = JsonSerializer.Deserialize<Point>(j.Destination),
                        Cargo = JsonSerializer.Deserialize<Cargo>(j.Cargo),
                        IsDeliveried = j.IsDeliveried,
                        Penalty = j.Penalty,
                    })
                .ToListAsync();
        }

        private void OnDataChanged(SCSTelemetry data, bool newTimestamp)
        {
            if (data.SpecialEventsValues.OnJob && CurrentJobInfo == null)
            {
                CurrentJobInfo = new()
                {
                    DeliveryTime = data.JobValues.DeliveryTime.Date,
                    PlanedDistance = data.JobValues.PlannedDistanceKm,
                    IsLoaded = data.JobValues.CargoLoaded,
                    IsLate = data.JobValues.RemainingDeliveryTime.Value < 0,
                    RemainingDeliveryTime = data.JobValues.RemainingDeliveryTime.Date,
                    Source = new Point
                    {
                        City = data.JobValues.CitySource,
                        Company = data.JobValues.CompanySource,
                    },
                    Destination = new Point
                    {
                        City = data.JobValues.CityDestination,
                        Company = data.JobValues.CompanyDestination
                    },
                    Cargo = new Cargo
                    {
                        Mass = data.JobValues.CargoValues.Mass,
                        Name = data.JobValues.CargoValues.Name,
                        Damage = data.JobValues.CargoValues.CargoDamage
                    }
                };
            }
            else if (data.SpecialEventsValues.OnJob && CurrentJobInfo != null)
            {
                CurrentJobInfo.IsLate = data.JobValues.RemainingDeliveryTime.Value < 0;
                CurrentJobInfo.RemainingDeliveryTime = data.JobValues.RemainingDeliveryTime.Date;
                CurrentJobInfo.DeliveryTime = data.JobValues.DeliveryTime.Date;
            }
            else
            {
                CurrentJobInfo = null;
            }

            if (data.SpecialEventsValues.JobDelivered && CurrentJobInfo != null)
            {
                CurrentJobInfo.Cargo.Damage = data.GamePlay.JobDelivered.CargoDamage;
                var jobInfo = new JobInfo
                {
                    AcceptedAt = data.GamePlay.JobDelivered.Started.Date,
                    FinishedAt = data.GamePlay.JobDelivered.Finished.Date,
                    PlannedIncome = CurrentJobInfo.Income,
                    Income = data.GamePlay.JobDelivered.Revenue,
                    Distance = data.GamePlay.JobDelivered.DistanceKm,
                    IsLate = CurrentJobInfo.IsLate,
                    Source = JsonSerializer.Serialize(CurrentJobInfo.Source),
                    Destination = JsonSerializer.Serialize(CurrentJobInfo.Destination),
                    Cargo = JsonSerializer.Serialize(CurrentJobInfo.Cargo),
                    IsDeliveried = true,
                    Penalty = 0
                };
                Save(jobInfo);
            }

            if (data.SpecialEventsValues.JobCancelled && CurrentJobInfo != null)
            {
                CurrentJobInfo.Cargo.Damage = data.GamePlay.JobDelivered.CargoDamage;
                var jobInfo = new JobInfo
                {
                    AcceptedAt = data.GamePlay.JobCancelled.Started.Date,
                    FinishedAt = data.GamePlay.JobCancelled.Finished.Date,
                    PlannedIncome = CurrentJobInfo.Income,
                    Income = 0,
                    Distance = CurrentJobInfo.PlanedDistance,
                    IsLate = CurrentJobInfo.IsLate,
                    Source = JsonSerializer.Serialize(CurrentJobInfo.Source),
                    Destination = JsonSerializer.Serialize(CurrentJobInfo.Destination),
                    Cargo = JsonSerializer.Serialize(CurrentJobInfo.Cargo),
                    IsDeliveried = false,
                    Penalty = data.GamePlay.JobCancelled.Penalty,
                };
                Save(jobInfo);
            }
        }

        private async void Save(JobInfo jobInfo)
        {
            using var scope = scopeFactory.CreateScope();
            var dbContext = scope.ServiceProvider.GetRequiredService<JobDbContext>();
            dbContext.Add(jobInfo);
            await dbContext.SaveChangesAsync();

        }

        public void Dispose()
        {
            telemetry.Data -= OnDataChanged;
        }
    }
}
