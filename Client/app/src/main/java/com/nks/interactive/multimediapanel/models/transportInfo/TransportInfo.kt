package com.nks.interactive.multimediapanel.models.transportInfo

data class TransportInfo(
    val trailerDamage: TrailerDamage? = null,
    val truckDamage: TruckDamage = TruckDamage(),
    val fuelInfo: FuelInfo = FuelInfo(),
    val errors: List<ErrorDto> = emptyList()
)