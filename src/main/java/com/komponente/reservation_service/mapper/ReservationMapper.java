package com.komponente.reservation_service.mapper;

import com.komponente.reservation_service.dto.ReservationCreateDto;
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

    public Reservation reservationCreateDtoToReservation(ReservationCreateDto reservationDto) {
        Optional<Vehicle> vehicle = vehicleRepo.findByPlateNumber(reservationDto.getPlateNumber());
        if(!vehicle.isPresent())
            throw new NotFoundException("Vehicle with plate number " + reservationDto.getPlateNumber() + " not found");
        Reservation reservation = new Reservation();
        reservation.setVehicle(vehicle.get());
        reservation.setStartDate(reservationDto.getStartDate());
        reservation.setEndDate(reservationDto.getEndDate());
        reservation.setUserId(reservationDto.getUserId());

        return reservation;
    }

    public ReservationDto reservationCreateDtoToReservationDto(ReservationCreateDto reservationDto) {
        ReservationDto reservation = new ReservationDto();
        reservation.setPlateNumber(reservationDto.getPlateNumber());
        reservation.setStartDate(reservationDto.getStartDate());
        reservation.setEndDate(reservationDto.getEndDate());

        return reservation;
    }
}
