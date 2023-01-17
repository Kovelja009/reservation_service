package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.dto.NotificationDto;
import com.komponente.reservation_service.dto.ReservationCreateDto;
import com.komponente.reservation_service.dto.ReservationDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.mapper.ReservationMapper;
import com.komponente.reservation_service.model.Reservation;
import com.komponente.reservation_service.repository.ReservationRepository;
import com.komponente.reservation_service.repository.VehicleRepository;
import com.komponente.reservation_service.service.ReservationService;
import com.komponente.reservation_service.user_sync_comm.dto.RankDto;
import com.komponente.reservation_service.user_sync_comm.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Setter
@Service
public class ReservationServiceImpl implements ReservationService {
    private RestTemplate userServiceRestTemplate;
    private ReservationRepository reservationRepo;
    private ReservationMapper reservationMapper;
    private VehicleRepository vehicleRepo;
    private RetryPatternHelper retryPatternHelper;

    @Override
    public ReservationDto createReservation(Long userId, ReservationCreateDto reservationCreateDto) {
        if(reservationCreateDto.getEndDate().compareTo(reservationCreateDto.getStartDate()) < 0)
            throw new IllegalArgumentException("End date must be after start date");

        ReservationDto reservationDto = reservationMapper.reservationCreateDtoToReservationDto(reservationCreateDto);
        UserDto userDto = retryPatternHelper.getUserByRetry(userId, userServiceRestTemplate);
        reservationDto.setUsername(userDto.getUsername());

//      get discount from user service
        RankDto rankDto = retryPatternHelper.getRankByRetry(userId, userServiceRestTemplate);

//      calculate price
        int days_between = (int) (((reservationDto.getEndDate().getTime() - reservationDto.getStartDate().getTime()) / (1000 * 60 * 60 * 24))+1);
        int price = vehicleRepo.findByPlateNumber(reservationCreateDto.getPlateNumber()).get().getPricePerDay() * days_between;
        int discount = rankDto.getDiscount();
        reservationDto.setPrice(price * (100 - discount) / 100);

        Reservation reservation = reservationMapper.reservationCreateDtoToReservation(userId, reservationCreateDto);
        reservation.setPrice(reservationDto.getPrice());
        reservation.setReminded(false);
        reservationRepo.save(reservation);

//      notify user service
        retryPatternHelper.updateUserByRetry(userId, days_between, userServiceRestTemplate);

        return reservationDto;
    }



    @Override
    public ReservationDto deleteReservation(ReservationDto reservationDto) {
        Optional<Reservation> reservation = reservationRepo.findReservationForDeleting(reservationDto.getPlateNumber(), reservationDto.getStartDate(), reservationDto.getEndDate());
        if(!reservation.isPresent())
            throw new IllegalArgumentException("Reservation not found");

//      notify user service
        int days_between = (int) (((reservationDto.getEndDate().getTime() - reservationDto.getStartDate().getTime()) / (1000 * 60 * 60 * 24))+1);
        days_between = -1*(days_between);
        retryPatternHelper.updateUserByRetry(reservation.get().getUserId(), days_between, userServiceRestTemplate);

        reservationRepo.delete(reservation.get());

        return reservationDto;
    }



    @Override
    public boolean setToReminded(ReservationDto reservationDto) {
        Optional<Reservation> reservation = reservationRepo.findReservationForDeleting(reservationDto.getPlateNumber(), reservationDto.getStartDate(), reservationDto.getEndDate());
        if(!reservation.isPresent())
            throw new NotFoundException("Reservation not found");

        Reservation r = reservation.get();
        r.setReminded(true);
        reservationRepo.save(r);
        return true;
    }

    @Override
    public List<NotificationDto> getReservationsToReminded() {
        Optional<List<Reservation>> reservations = reservationRepo.findReservationForRemind();
        List<NotificationDto> reminders = new ArrayList<>();
        if(!reservations.isPresent() || reservations.get().isEmpty())
            throw new NotFoundException("No reservations to remind");
        for(Reservation reserv:reservations.get()){
            UserDto userDto = retryPatternHelper.getUserByRetry(reserv.getUserId(), userServiceRestTemplate);
            if(userDto==null)
                throw new NotFoundException("No user found!");
            reserv.setReminded(true);
            reservationRepo.save(reserv);
            reminders.add(reservationMapper.notificationFromReservation(userDto,reserv));
        }
        return reminders;
    }

    @Override
    public List<ReservationDto> getReservations() {
        Optional<List<Reservation>> reservation = reservationRepo.findAllReservations();
        if(!reservation.isPresent() || reservation.get().isEmpty())
            throw new NotFoundException("No reservations found");
        return reservation.get().stream().map(reservationMapper::reservationToReservationDto).collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getReservationsForUser(Long userId) {
        Optional<List<Reservation>> reservation = reservationRepo.findByUserId(userId);
        if(!reservation.isPresent() || reservation.get().isEmpty())
            throw new NotFoundException("No reservations found");
        return reservation.get().stream().map(reservationMapper::reservationToReservationDto).collect(Collectors.toList());
    }

}
