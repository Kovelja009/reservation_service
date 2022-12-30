package com.komponente.reservation_service.mapper;

import com.komponente.reservation_service.dto.ReservationDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.model.Reservation;
import com.komponente.reservation_service.model.Vehicle;
import com.komponente.reservation_service.repository.VehicleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReservationMapper {
    private VehicleRepository vehicleRepo;

    public ReservationMapper(VehicleRepository vehicleRepository) {
        this.vehicleRepo = vehicleRepository;
    }

    public Reservation reservationDtoToReservation(ReservationDto reservationDto) {
        Optional<Vehicle> vehicle = vehicleRepo.findByPlateNumber(reservationDto.getPlateNumber());
        if(vehicle.isEmpty())
            throw new NotFoundException("Vehicle with plate number " + reservationDto.getPlateNumber() + " not found");
        Reservation reservation = new Reservation();
        reservation.setVehicle(vehicle.get());
        reservation.setStartDate(reservationDto.getStartDate());
        reservation.setEndDate(reservationDto.getEndDate());
//        TODO u servisu dodati ClientId preko restTemplate-a
        return reservation;
    }
}
