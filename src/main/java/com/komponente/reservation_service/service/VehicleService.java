package com.komponente.reservation_service.service;

import com.komponente.reservation_service.dto.VehicleDto;

public interface VehicleService {
    VehicleDto addVehicle(VehicleDto vehicleDto);
    void deleteVehicle(String plateNumber);
    String changePriceForModel(String model, String company, int newPrice);
}
