package com.komponente.reservation_service.IntegrationTests;

import com.komponente.reservation_service.dto.ReservationDto;
import com.komponente.reservation_service.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReservationServiceIT {

    @Autowired
    private ReservationServiceImpl reservationService;

    @Test
    public void deleteReservationTest(){
        ReservationDto res = new ReservationDto();
        res.setPlateNumber("nonexistent");
        res.setStartDate(Date.valueOf("2021-05-01"));
        res.setEndDate(Date.valueOf("2021-05-02"));
        res.setUsername("nonexistent");
        res.setPrice(-1000);

        assertThrows(Exception.class, () -> reservationService.deleteReservation(res));
    }

}
