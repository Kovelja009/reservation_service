package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.dto.ReservationCreateDto;
import com.komponente.reservation_service.dto.ReservationDto;
import com.komponente.reservation_service.dto.ReviewDto;
import com.komponente.reservation_service.mapper.ReservationMapper;
import com.komponente.reservation_service.model.Reservation;
import com.komponente.reservation_service.repository.ReservationRepository;
import com.komponente.reservation_service.repository.VehicleRepository;
import com.komponente.reservation_service.service.ReservationService;
import com.komponente.reservation_service.user_sync_comm.dto.RankDto;
import com.komponente.reservation_service.user_sync_comm.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {
    private RestTemplate userServiceRestTemplate;
    private ReservationRepository reservationRepo;
    private ReservationMapper reservationMapper;
    private VehicleRepository vehicleRepo;

    @Override
    public ReservationDto createReservation(ReservationCreateDto reservationCreateDto) {
        if(reservationCreateDto.getEndDate().compareTo(reservationCreateDto.getStartDate()) < 0)
            throw new IllegalArgumentException("End date must be after start date");

        ReservationDto reservationDto = reservationMapper.reservationCreateDtoToReservationDto(reservationCreateDto);
        ResponseEntity<UserDto> userDto = userServiceRestTemplate.exchange("/user/id?id="+reservationCreateDto.getUserId(), HttpMethod.GET, null, UserDto.class);
        reservationDto.setUsername(userDto.getBody().getUsername());
//        get discount from user service
        int days_between = (int) (((reservationDto.getEndDate().getTime() - reservationDto.getStartDate().getTime()) / (1000 * 60 * 60 * 24))+1);

        ResponseEntity<RankDto> discountResponseEntity = userServiceRestTemplate.exchange("/client/get_rank?user_id="+reservationCreateDto.getUserId(), HttpMethod.GET, null, RankDto.class);

//        calculate price
        int price = vehicleRepo.findByPlateNumber(reservationCreateDto.getPlateNumber()).get().getPricePerDay() * days_between;
        int discount = discountResponseEntity.getBody().getDiscount();
        reservationDto.setPrice(price * (100 - discount) / 100);

        Reservation reservation = reservationMapper.reservationCreateDtoToReservation(reservationCreateDto);
        reservation.setPrice(reservationDto.getPrice());
        reservationRepo.save(reservation);

//      notify user service
        userServiceRestTemplate.exchange("/client/update_rent_days?user_id="+reservation.getUserId().toString() + "&rentDays=" + days_between, HttpMethod.POST, null, Integer.class);

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
        userServiceRestTemplate.exchange("/client/update_rent_days?user_id="+reservation.get().getUserId().toString() + "&rentDays=" + days_between, HttpMethod.POST, null, Integer.class);

        reservationRepo.delete(reservation.get());

        return reservationDto;
    }

    @Override
    public List<ReservationDto> getReservationsToReminded() {
        Optional<List<Reservation>> reservation = reservationRepo.findReservationForRemind();
        if(!reservation.isPresent() || reservation.get().isEmpty())
            throw new IllegalArgumentException("No reviews found");
        return reservation.get().stream().map(reservationMapper::reservationToReservationDto).collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getReservations() {
        Optional<List<Reservation>> reservation = reservationRepo.findAllReservations();
        if(!reservation.isPresent() || reservation.get().isEmpty())
            throw new IllegalArgumentException("No reviews found");
        return reservation.get().stream().map(reservationMapper::reservationToReservationDto).collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getReservationsForUser(Long userId) {
        Optional<List<Reservation>> reservation = reservationRepo.findByUserId(userId);
        if(!reservation.isPresent() || reservation.get().isEmpty())
            throw new IllegalArgumentException("No reviews found");
        return reservation.get().stream().map(reservationMapper::reservationToReservationDto).collect(Collectors.toList());
    }
}
