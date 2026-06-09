using CommonDataModule.Models;
using SCSSdkClient;
using SCSSdkClient.Object;
using System;
using System.Collections.Generic;
using System.Text;

namespace CommonDataModule.Services
{
    public sealed class CommonDataService : IDisposable
    {
        private readonly SCSSdkTelemetry telemetry;

        public CommonData CommonData { get; set; } = new();
        public CommonDataService(SCSSdkTelemetry telemetry)
        {
            this.telemetry = telemetry;
            this.telemetry.Data += OnDataChanged;
        }

        private void OnDataChanged(SCSTelemetry data, bool newTimestamp)
        {
            CommonData.CurrentGameTime = data.CommonValues.GameTime.Date;
            CommonData.NextRestStopTime = data.CommonValues.NextRestStopTime.Date;
            CommonData.NextRestStopAfter = TimeOnly.FromDateTime(data.CommonValues.NextRestStop.Date);
        }

        public void Dispose()
        {
            telemetry.Data -= OnDataChanged;
        }
    }
}
