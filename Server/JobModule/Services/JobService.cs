using JobModule.Models;
using SCSSdkClient;
using SCSSdkClient.Object;

namespace JobModule.Services
{
    public sealed class JobService : IDisposable
    {
        private readonly SCSSdkTelemetry telemetry;
        public CurrentJobInfo? CurrentJobInfo { get; set; } = null;
        public JobService(SCSSdkTelemetry telemetry) 
        {
            this.telemetry = telemetry;
            this.telemetry.Data += OnDataChanged;
        }

        private void OnDataChanged(SCSTelemetry data, bool newTimestamp)
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
                },
                Income = data.JobValues.Income
            };
        }

        public void Dispose()
        {
            telemetry.Data -= OnDataChanged;
        }
    }
}
