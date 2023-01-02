package com.komponente.reservation_service.repository;

import com.komponente.reservation_service.model.Company;
import com.komponente.reservation_service.model.Model;
import com.komponente.reservation_service.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByPlateNumber(String plateNumber);
    Optional<List<Vehicle>> findByModelAndAndCompany(Model model, Company company);
}
