package com.komponente.reservation_service.controller;

import com.komponente.reservation_service.dto.*;
import com.komponente.reservation_service.security.CheckSecurity;
import com.komponente.reservation_service.security.service.TokenService;
import com.komponente.reservation_service.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private TokenService tokenService;

    private ReservationService reservationService;
    @PostMapping("/create")
    @CheckSecurity(roles = {"ROLE_CLIENT"})
    public ResponseEntity<ReservationDto> createReservation(@RequestHeader("Authorization") String authorization, @RequestBody ReservationCreateDto reservationCreateDto) {
        return new ResponseEntity<>(reservationService.createReservation(tokenService.getIdFromToken(authorization), reservationCreateDto), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @CheckSecurity(roles = {"ROLE_CLIENT", "ROLE_MANAGER"})
    public ResponseEntity<ReservationDto> deleteReservation(@RequestHeader("Authorization") String authorization, @RequestBody ReservationDto reservationDto) {
        return new ResponseEntity<>(reservationService.deleteReservation(reservationDto), HttpStatus.OK);
    }

    @GetMapping("/id")
    public ResponseEntity<ReservationListDto> getReservationsById(@RequestParam Long userId) {
        return new ResponseEntity<>(new ReservationListDto(reservationService.getReservationsForUser(userId)), HttpStatus.OK);
    }
    @PostMapping("/reminded")
    public ResponseEntity<Boolean> setToReminded(@RequestBody ReservationDto reservationDto) {
        return new ResponseEntity<>(reservationService.setToReminded(reservationDto),HttpStatus.OK);
    }

    @GetMapping("/remind")
    public ResponseEntity<NotificationListDto> getReservationsToRemind() {
        try {
            return new ResponseEntity<>(new NotificationListDto(reservationService.getReservationsToReminded()), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new NotificationListDto(new ArrayList<>()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<ReservationListDto> getReservations() {
        return new ResponseEntity<>(new ReservationListDto(reservationService.getReservations()), HttpStatus.OK);
    }

    @GetMapping("/by_user")
    @CheckSecurity(roles = {"ROLE_CLIENT", "ROLE_MANAGER"})
    public ResponseEntity<ReservationListDto> getReservationsByUser(@RequestHeader("Authorization") String authorization) {
        if(tokenService.parseToken(authorization.split(" ")[1]).get("role", String.class).equals("ROLE_CLIENT"))
            return new ResponseEntity<>(new ReservationListDto(reservationService.getReservationsForUser(tokenService.getIdFromToken(authorization))), HttpStatus.OK);
        return new ResponseEntity<>(new ReservationListDto(reservationService.getReservationsForManager(tokenService.getIdFromToken(authorization))), HttpStatus.OK);
    }
}
