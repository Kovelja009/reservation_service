package com.komponente.reservation_service.service;

import com.komponente.reservation_service.dto.ReservationCreateDto;
import com.komponente.reservation_service.dto.ReservationDto;

public interface ReservationService {
    ReservationDto createReservation(ReservationCreateDto reservationDto);
    ReservationDto deleteReservation(ReservationDto reservationDto);
}
