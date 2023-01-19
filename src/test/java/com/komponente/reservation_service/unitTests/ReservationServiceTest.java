package com.komponente.reservation_service.unitTests;

import com.komponente.reservation_service.dto.ReservationCreateDto;
import com.komponente.reservation_service.dto.ReservationDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.mapper.ReservationMapper;
import com.komponente.reservation_service.model.Reservation;
import com.komponente.reservation_service.model.Vehicle;
import com.komponente.reservation_service.repository.ReservationRepository;
import com.komponente.reservation_service.repository.VehicleRepository;
import com.komponente.reservation_service.service.impl.ReservationServiceImpl;
import com.komponente.reservation_service.service.impl.RetryPatternHelper;
import com.komponente.reservation_service.user_sync_comm.dto.RankDto;
import com.komponente.reservation_service.user_sync_comm.dto.UserDto;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ReservationServiceTest {
    @MockBean
    private RestTemplate userServiceRestTemplate;
    @MockBean
    private ReservationRepository reservationRepo;

    @MockBean
    private ReservationMapper reservationMapper;

    @MockBean
    private VehicleRepository vehicleRepo;

    @MockBean
    private Retry serviceRetry;

    @MockBean
    private RetryPatternHelper retryPatternHelper;

    @Autowired
    private ReservationServiceImpl reservationService;


    @Test
    public void createReservationTestInvalidDates() {
        ReservationCreateDto reservationDto = new ReservationCreateDto();
        reservationDto.setPlateNumber("KV-054-TO");
        reservationDto.setStartDate(Date.valueOf("2021-05-02"));
        reservationDto.setEndDate(Date.valueOf("2021-05-01"));
        Long userId = 1L;

        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(userId, reservationDto));

        verify(reservationMapper, never()).reservationCreateDtoToReservationDto(reservationDto);
        verify(reservationMapper, never()).reservationCreateDtoToReservation(any(), any());
        verify(retryPatternHelper,never()).getUserByRetry(userId, userServiceRestTemplate);
        verify(retryPatternHelper,never()).getRankByRetry(userId, userServiceRestTemplate);
        verify(vehicleRepo, never()).findByPlateNumber("KV-054-TO");
        verify(retryPatternHelper, never()).updateUserByRetry(userId, 2, userServiceRestTemplate);
        verify(reservationRepo, never()).save(any());
    }

    @Test
    public void createReservationTest() {
        ReservationCreateDto reservationDto = new ReservationCreateDto();
        reservationDto.setPlateNumber("KV-054-TO");
        reservationDto.setStartDate(Date.valueOf("2021-05-01"));
        reservationDto.setEndDate(Date.valueOf("2021-05-02"));
        Long userId = 1L;

        UserDto userDto = new UserDto();
        userDto.setUsername("username");
        userDto.setFirstName("firstName");
        userDto.setLastName("lastName");
        userDto.setEmail("email@gmail.com");

        RankDto rankDto = new RankDto();
        rankDto.setName("platinum");
        rankDto.setDiscount(10);
        rankDto.setMaxDays(30);
        rankDto.setMinDays(10);

        ReservationDto res = new ReservationDto();
        res.setPlateNumber("KV-054-TO");
        res.setStartDate(Date.valueOf("2021-05-01"));
        res.setEndDate(Date.valueOf("2021-05-02"));
        res.setUsername("username");

        Optional<Vehicle> vehicle = Optional.of(new Vehicle());
        vehicle.get().setPricePerDay(1000);



        when(retryPatternHelper.getUserByRetry(userId, userServiceRestTemplate)).thenReturn(userDto);
        when(retryPatternHelper.getRankByRetry(userId, userServiceRestTemplate)).thenReturn(rankDto);
        when(vehicleRepo.findByPlateNumber("KV-054-TO")).thenReturn(vehicle);
        when(reservationMapper.reservationCreateDtoToReservationDto(reservationDto)).thenReturn(res);
        when(reservationMapper.reservationCreateDtoToReservation(any(), any())).thenReturn(new Reservation());

        ReservationDto reservation = reservationService.createReservation(userId, reservationDto);
        assertEquals(1800, reservation.getPrice());
        assertEquals(userDto.getUsername(), reservation.getUsername());
        assertEquals(reservationDto.getPlateNumber(), reservation.getPlateNumber());
        assertEquals(reservationDto.getStartDate(), reservation.getStartDate());
        assertEquals(reservationDto.getEndDate(), reservation.getEndDate());

        verify(reservationMapper, times(1)).reservationCreateDtoToReservationDto(reservationDto);
        verify(reservationMapper, times(1)).reservationCreateDtoToReservation(any(), any());
        verify(retryPatternHelper, times(1)).getUserByRetry(userId, userServiceRestTemplate);
        verify(retryPatternHelper, times(1)).getRankByRetry(userId, userServiceRestTemplate);
        verify(vehicleRepo, times(1)).findByPlateNumber("KV-054-TO");
        verify(retryPatternHelper, times(1)).updateUserByRetry(userId, 2, userServiceRestTemplate);
        verify(reservationRepo, times(1)).save(any());

    }

    @Test
    public void deleteNonExistingReservationTest(){
        ReservationDto res = new ReservationDto();
        res.setPlateNumber("KV-054-TO");
        res.setStartDate(Date.valueOf("2021-05-01"));
        res.setEndDate(Date.valueOf("2021-05-02"));
        res.setUsername("username");
        res.setPrice(1000);

        Optional<Reservation> reservation = Optional.empty();
        when(reservationRepo.findReservationForDeleting(any(), any(), any())).thenReturn(reservation);

        assertThrows(IllegalArgumentException.class, () -> reservationService.deleteReservation(res));

        verify(reservationRepo, times(1)).findReservationForDeleting(any(), any(), any());
    }

    @Test
    public void deleteReservationTest(){
        ReservationDto res = new ReservationDto();
        res.setPlateNumber("KV-054-TO");
        res.setStartDate(Date.valueOf("2021-05-01"));
        res.setEndDate(Date.valueOf("2021-05-02"));
        res.setUsername("username");
        res.setPrice(1000);

        Optional<Reservation> reservation = Optional.of(new Reservation());
        reservation.get().setPrice(1000);
        reservation.get().setStartDate(Date.valueOf("2021-05-01"));
        reservation.get().setEndDate(Date.valueOf("2021-05-02"));
        reservation.get().setUserId(1L);

        when(reservationRepo.findReservationForDeleting(res.getPlateNumber(), res.getStartDate(), res.getEndDate())).thenReturn(reservation);
        when(reservationMapper.reservationToReservationDto(any())).thenReturn(res);
        assertEquals(res, reservationService.deleteReservation(res));

        verify(reservationRepo, times(1)).findReservationForDeleting(res.getPlateNumber(), res.getStartDate(), res.getEndDate());
        verify(reservationRepo, times(1)).delete(any());
        verify(retryPatternHelper, times(1)).updateUserByRetry(reservation.get().getUserId(), -2, userServiceRestTemplate);
    }

    @Test
    public void getReservationTestNoReservations(){
        List<Reservation> resList = new ArrayList<>();
        when(reservationRepo.findAllReservations()).thenReturn(Optional.of(resList));

        assertThrows(NotFoundException.class, () -> reservationService.getReservations());

        verify(reservationRepo, times(1)).findAllReservations();
        verify(reservationMapper, never()).reservationToReservationDto(any());
    }

    @Test
    public void getReservationTest(){
        List<Reservation> resList = returnResList();

        ReservationDto res = returnRes();

        when(reservationRepo.findAllReservations()).thenReturn(Optional.of(resList));
        when(reservationMapper.reservationToReservationDto(any())).thenReturn(res);


        List<ReservationDto> list = reservationService.getReservations();
        assertEquals(1, list.size());
        assertEquals(res , list.get(0));


        verify(reservationRepo, times(1)).findAllReservations();
        verify(reservationMapper, times(1)).reservationToReservationDto(any());
    }

    @Test
    public void getReservationForUserTestNoReservation(){
        List<Reservation> resList = new ArrayList<>();
        when(reservationRepo.findByUserId(any())).thenReturn(Optional.of(resList));

        assertThrows(NotFoundException.class, () -> reservationService.getReservationsForUser(1L));

        verify(reservationRepo, times(1)).findByUserId(any());
        verify(reservationMapper, never()).reservationToReservationDto(any());
    }

    @Test
    public void getReservationsForUserTest(){
        List<Reservation> resList = returnResList();

        ReservationDto res = returnRes();

        when(reservationRepo.findByUserId(any())).thenReturn(Optional.of(resList));
        when(reservationMapper.reservationToReservationDto(any())).thenReturn(res);

        List<ReservationDto> list = reservationService.getReservationsForUser(1L);
        assertEquals(1, list.size());
        assertEquals(res , list.get(0));
    }

    private List<Reservation> returnResList(){
        List<Reservation> resList = new ArrayList<>();
        Reservation reservation = new Reservation();
        reservation.setPrice(1000);
        reservation.setStartDate(Date.valueOf("2021-05-01"));
        reservation.setEndDate(Date.valueOf("2021-05-02"));
        reservation.setUserId(1L);
        reservation.setReminded(false);
        reservation.setVehicle(new Vehicle());
        resList.add(reservation);

        return resList;
    }

    private ReservationDto returnRes(){
        ReservationDto res = new ReservationDto();
        res.setPlateNumber("KV-054-TO");
        res.setStartDate(Date.valueOf("2021-05-01"));
        res.setEndDate(Date.valueOf("2021-05-02"));
        res.setUsername("username");
        res.setPrice(1000);
        return res;
    }

}
