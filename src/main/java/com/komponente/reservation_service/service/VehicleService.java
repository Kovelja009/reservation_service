package com.komponente.reservation_service.service;

import com.komponente.reservation_service.dto.VehicleDto;

import java.sql.Date;
import java.util.List;

public interface VehicleService {
    VehicleDto addVehicle(VehicleDto vehicleDto);
    void deleteVehicle(String plateNumber);
    String changePriceForModel(String model, String company, int newPrice);
    List<VehicleDto> getAllAvailableVehicles(String city, String company, Date startDate, Date endDate);
}
