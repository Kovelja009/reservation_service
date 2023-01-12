package com.komponente.reservation_service.service;

import com.komponente.reservation_service.dto.ReservationCreateDto;
import com.komponente.reservation_service.dto.ReservationDto;

import java.util.List;

public interface ReservationService {
    ReservationDto createReservation(ReservationCreateDto reservationDto);
    ReservationDto deleteReservation(ReservationDto reservationDto);

    List<ReservationDto> getReservationsToReminded();

    List<ReservationDto> getReservations();

    List<ReservationDto> getReservationsForUser(Long userId);
}
