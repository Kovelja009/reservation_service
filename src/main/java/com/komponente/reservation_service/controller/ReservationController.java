package com.komponente.reservation_service.controller;

import com.komponente.reservation_service.dto.ReservationCreateDto;
import com.komponente.reservation_service.dto.ReservationDto;
import com.komponente.reservation_service.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private ReservationService reservationService;
    @PostMapping("/create")
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationCreateDto reservationCreateDto) {
        return new ResponseEntity<>(reservationService.createReservation(reservationCreateDto), HttpStatus.OK);
    }
}
