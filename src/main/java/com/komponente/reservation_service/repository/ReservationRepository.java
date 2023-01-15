package com.komponente.reservation_service.repository;

import com.komponente.reservation_service.model.Reservation;
import com.komponente.reservation_service.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<List<Reservation>> findByUserId(Long userId);

    @Query(value = "select * from reservations.reservation", nativeQuery = true)
    Optional<List<Reservation>> findAllReservations();

    @Query(value = "select * from reservations.reservation where start_date < DATEDIFF(start_date, CURDATE()) and reminded=false", nativeQuery = true)
    Optional<List<Reservation>> findReservationForRemind();

    Optional<List<Reservation>> findByVehicle(Vehicle vehicle);

    @Query(value = "select * from reservations.reservation where vehicle_plate_number=?1 and start_date=?2 and end_date=?3", nativeQuery = true)
    Optional<Reservation> findReservationForDeleting(String vehiclePlateNumber, Date startDate, Date endDate);
}
