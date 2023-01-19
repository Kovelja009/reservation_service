package com.komponente.reservation_service.repository;


import com.komponente.reservation_service.model.Company;
import com.komponente.reservation_service.model.Model;
import com.komponente.reservation_service.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByPlateNumber(String plateNumber);
    Optional<List<Vehicle>> findByModelAndAndCompany(Model model, Company company);

    @Query(value = "select v.plate_number, v.price_per_day, v.city_id, v.company_id, v.model_id from reservations.vehicle v join reservations.city c on v.city_id = c.id where c.city=?1 order by v.price_per_day asc", nativeQuery = true)
    Optional<List<Vehicle>> findByCity(String city);

    @Query(value = "select v.plate_number, v.price_per_day, v.city_id, v.company_id, v.model_id from reservations.vehicle v join reservations.company c on v.company_id = c.id where c.name=?1 order by v.price_per_day asc", nativeQuery = true)
    Optional<List<Vehicle>> findByCompany(String company);

    @Query(value = "select v.plate_number, v.price_per_day, v.city_id, v.company_id, v.model_id from reservations.vehicle v join reservations.city c on v.city_id = c.id join reservations.company co on v.company_id = co.id where c.city=?1 and co.name=?2 order by v.price_per_day asc", nativeQuery = true)
    Optional<List<Vehicle>> findByCityAndCompany(String city, String company);

    @Query(value = "select * from reservations.vehicle order by price_per_day asc", nativeQuery = true)
    Optional<List<Vehicle>> findAllAvailableVehicles();

    @Query(value = "select v.plate_number, v.price_per_day, v.city_id, v.company_id, v.model_id from reservations.vehicle v join reservations.city c on v.city_id = c.id where c.city=?1 order by v.price_per_day desc", nativeQuery = true)
    Optional<List<Vehicle>> findByCityDesc(String city);

    @Query(value = "select v.plate_number, v.price_per_day, v.city_id, v.company_id, v.model_id from reservations.vehicle v join reservations.company c on v.company_id = c.id where c.name=?1 order by v.price_per_day desc", nativeQuery = true)
    Optional<List<Vehicle>> findByCompanyDesc(String company);

    @Query(value = "select v.plate_number, v.price_per_day, v.city_id, v.company_id, v.model_id from reservations.vehicle v join reservations.city c on v.city_id = c.id join reservations.company co on v.company_id = co.id where c.city=?1 and co.name=?2 order by v.price_per_day desc", nativeQuery = true)
    Optional<List<Vehicle>> findByCityAndCompanyDesc(String city, String company);

    @Query(value = "select * from reservations.vehicle order by price_per_day desc", nativeQuery = true)
    Optional<List<Vehicle>> findAllAvailableVehiclesDesc();

}
