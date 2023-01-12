package com.komponente.reservation_service.controller;

import com.komponente.reservation_service.dto.ReservationCreateDto;
import com.komponente.reservation_service.dto.ReservationDto;
import com.komponente.reservation_service.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.List;

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

    @GetMapping("/id")
    public ResponseEntity<List<ReservationDto>> getReservationsById(@RequestParam Long userId) {
        return new ResponseEntity<>(reservationService.getReservationsForUser(userId), HttpStatus.OK);
    }
    //TODO dodati put za rezervacije reminded true
    @PutMapping("/reminded")
    public ResponseEntity<Boolean> modifyPrice(@RequestParam @Valid Long id) {
        return new ResponseEntity<>(true,HttpStatus.OK);
    }


    @GetMapping("/remind")
    public ResponseEntity<List<ReservationDto>> getReservationsToRemind() {
        return new ResponseEntity<>(reservationService.getReservationsToReminded(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ReservationDto>> getReservations() {
        return new ResponseEntity<>(reservationService.getReservations(), HttpStatus.OK);
    }
}
