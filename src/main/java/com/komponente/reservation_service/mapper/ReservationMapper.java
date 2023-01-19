package com.komponente.reservation_service.mapper;

import com.komponente.reservation_service.dto.NotificationDto;
import com.komponente.reservation_service.dto.ReservationCreateDto;
import com.komponente.reservation_service.dto.ReservationDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.model.Reservation;
import com.komponente.reservation_service.model.Vehicle;
import com.komponente.reservation_service.repository.VehicleRepository;
import com.komponente.reservation_service.user_sync_comm.dto.UserDto;
import com.sun.xml.bind.v2.TODO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReservationMapper {
    private VehicleRepository vehicleRepo;

    public ReservationMapper(VehicleRepository vehicleRepository) {
        this.vehicleRepo = vehicleRepository;
    }

    public Reservation reservationCreateDtoToReservation(Long userId, ReservationCreateDto reservationDto) {
        Optional<Vehicle> vehicle = vehicleRepo.findByPlateNumber(reservationDto.getPlateNumber());
        if(!vehicle.isPresent())
            throw new NotFoundException("Vehicle with plate number " + reservationDto.getPlateNumber() + " not found");
        Reservation reservation = new Reservation();
        reservation.setVehicle(vehicle.get());
        reservation.setStartDate(reservationDto.getStartDate());
        reservation.setEndDate(reservationDto.getEndDate());
        reservation.setUserId(userId);

        return reservation;
    }

    public NotificationDto notificationFromReservation(UserDto userDto,Reservation reservation){
        return new NotificationDto(userDto.getEmail(),"reservation",
                "Dear %s %s,\n Your reservation in %s for vehicle %s is in 3 days",
                userDto.getFirstName(), userDto.getLastName(), reservation.getVehicle().getCompany().getName(),
                null,reservation.getVehicle().getModel().getModel(),null);
    }

    public NotificationDto notificationFromReservationReservationConf(UserDto userDto,Reservation reservation){
        return new NotificationDto(userDto.getEmail(),"reservationConf",
                "Dear %s %s,\n Your reservation in %s for vehicle %s is confirmed",
                userDto.getFirstName(), userDto.getLastName(), reservation.getVehicle().getCompany().getName(),
                null,reservation.getVehicle().getModel().getModel(),null);
    }

    public ReservationDto reservationCreateDtoToReservationDto(ReservationCreateDto reservationDto) {
        ReservationDto reservation = new ReservationDto();
        reservation.setPlateNumber(reservationDto.getPlateNumber());
        reservation.setStartDate(reservationDto.getStartDate());
        reservation.setEndDate(reservationDto.getEndDate());

        return reservation;
    }
    //TODO izmeniti username
    public ReservationDto reservationToReservationDto(Reservation reservation){
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setPrice(reservation.getPrice());
        reservationDto.setUsername(reservation.getUserId().toString());
        reservationDto.setPlateNumber(reservation.getVehicle().getPlateNumber());
        reservationDto.setStartDate(reservation.getStartDate());
        reservationDto.setEndDate(reservation.getEndDate());
        return reservationDto;
    }

    public NotificationDto notificationFromReservationCancel(UserDto userDto, Reservation reservation) {
        return new NotificationDto(userDto.getEmail(),"cancel_reservation",
                "Dear %s %s,\n Your reservation in %s for vehicle %s is canceled",
                userDto.getFirstName(), userDto.getLastName(), reservation.getVehicle().getCompany().getName(),
                null,reservation.getVehicle().getModel().getModel(),null);
    }
}
