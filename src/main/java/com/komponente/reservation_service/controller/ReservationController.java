package com.komponente.reservation_service.controller;

import com.komponente.reservation_service.dto.ReservationCreateDto;
import com.komponente.reservation_service.dto.ReservationDto;
import com.komponente.reservation_service.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private ReservationService reservationService;
    @PostMapping("/create")
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationCreateDto reservationCreateDto) {
        return new ResponseEntity<>(reservationService.createReservation(reservationCreateDto), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ReservationDto> deleteReservation(@RequestBody ReservationDto reservationDto) {
        return new ResponseEntity<>(reservationService.deleteReservation(reservationDto), HttpStatus.OK);
    }
}
