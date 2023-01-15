package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.dto.NotificationDto;
import com.komponente.reservation_service.dto.ReservationCreateDto;
import com.komponente.reservation_service.dto.ReservationDto;
import com.komponente.reservation_service.dto.ReviewDto;
import com.komponente.reservation_service.exceptions.ForbiddenException;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.mapper.ReservationMapper;
import com.komponente.reservation_service.model.Reservation;
import com.komponente.reservation_service.repository.ReservationRepository;
import com.komponente.reservation_service.repository.VehicleRepository;
import com.komponente.reservation_service.service.ReservationService;
import com.komponente.reservation_service.user_sync_comm.dto.RankDto;
import com.komponente.reservation_service.user_sync_comm.dto.UserDto;
import com.komponente.reservation_service.user_sync_comm.dto.UserIdDto;
import io.github.resilience4j.retry.Retry;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.ArrayList;
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
    private Retry serviceRetry;

    @Override
    public ReservationDto createReservation(Long userId, ReservationCreateDto reservationCreateDto) {
        if(reservationCreateDto.getEndDate().compareTo(reservationCreateDto.getStartDate()) < 0)
            throw new IllegalArgumentException("End date must be after start date");

        ReservationDto reservationDto = reservationMapper.reservationCreateDtoToReservationDto(reservationCreateDto);
        UserDto userDto = Retry.decorateSupplier(serviceRetry, () -> getUser(userId, userServiceRestTemplate)).get();
        reservationDto.setUsername(userDto.getUsername());

//      get discount from user service
        RankDto rankDto = Retry.decorateSupplier(serviceRetry, () -> getRank(userId, userServiceRestTemplate)).get();

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
        Retry.decorateSupplier(serviceRetry, () -> userServiceRestTemplate.exchange("/client/update_rent_days?user_id="+reservation.getUserId().toString() + "&rentDays=" + days_between, HttpMethod.POST, null, Integer.class));

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
        int finalDays_between = days_between;
        Retry.decorateSupplier(serviceRetry, () -> updateRentDays(reservation.get().getUserId(), finalDays_between, userServiceRestTemplate));

        reservationRepo.delete(reservation.get());

        return reservationDto;
    }



    @Override
    public boolean setToReminded(ReservationDto reservationDto) {
        Optional<Reservation> reservation = reservationRepo.findReservationForDeleting(reservationDto.getPlateNumber(), reservationDto.getStartDate(), reservationDto.getEndDate());
        if(!reservation.isPresent())
            throw new IllegalArgumentException("Reservation not found");

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
            throw new IllegalArgumentException("No reservations to remind");
        for(Reservation reserv:reservations.get()){
            UserDto userDto = Retry.decorateSupplier(serviceRetry, () -> getUser(reserv.getUserId(), userServiceRestTemplate)).get();
            if(userDto==null)
                throw new IllegalArgumentException("No user found ");
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

    public static UserDto getUser(Long userId, RestTemplate userServiceRestTemplate){
        try {
            return userServiceRestTemplate.exchange("/user/id?id="+userId, HttpMethod.GET, null, UserDto.class).getBody();

        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NotFoundException("User with id " + userId + " not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
            if(e.getStatusCode().equals(HttpStatus.FORBIDDEN)){
                throw new ForbiddenException("Forbidden");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }

    public static RankDto getRank(Long userId, RestTemplate userServiceRestTemplate){
        try {
            return userServiceRestTemplate.exchange("/client/get_rank?user_id="+userId, HttpMethod.GET, null, RankDto.class).getBody();

        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NotFoundException("User with id " + userId + " not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
            if(e.getStatusCode().equals(HttpStatus.FORBIDDEN)){
                throw new ForbiddenException("Forbidden");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }

    public static Integer updateRentDays(Long userId, int days, RestTemplate userServiceRestTemplate){
        try {
            userServiceRestTemplate.exchange("/client/update_rent_days?user_id="+userId.toString() + "&rentDays=" + days, HttpMethod.POST, null, Integer.class).getBody();

        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NotFoundException("Not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
            if(e.getStatusCode().equals(HttpStatus.FORBIDDEN)){
                throw new ForbiddenException("Forbidden");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }

    public static UserIdDto getuUserId(String username, RestTemplate userServiceRestTemplate){
        try {
            userServiceRestTemplate.exchange("/user/username?username="+username, HttpMethod.GET, null, UserIdDto.class).getBody();
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NotFoundException("Not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
            if(e.getStatusCode().equals(HttpStatus.FORBIDDEN)){
                throw new ForbiddenException("Forbidden");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }

    public static String getCompanyId(Long userId, RestTemplate userServiceRestTemplate) {
        try {
            return userServiceRestTemplate.exchange("/manager/get_company?user_id="+userId.toString(), HttpMethod.GET, null, String.class).getBody();
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NotFoundException("Not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
            if(e.getStatusCode().equals(HttpStatus.FORBIDDEN)){
                throw new ForbiddenException("Forbidden");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }
}
