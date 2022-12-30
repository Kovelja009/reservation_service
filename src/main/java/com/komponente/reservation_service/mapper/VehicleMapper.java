package com.komponente.reservation_service.mapper;

import com.komponente.reservation_service.dto.VehicleDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.model.City;
import com.komponente.reservation_service.model.Company;
import com.komponente.reservation_service.model.Model;
import com.komponente.reservation_service.model.Vehicle;
import com.komponente.reservation_service.repository.CityRepository;
import com.komponente.reservation_service.repository.CompanyRepository;
import com.komponente.reservation_service.repository.ModelRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VehicleMapper {
    private CityRepository cityRepo;
    private CompanyRepository companyRepo;
    private ModelRepository modelRepo;

    public VehicleMapper(CityRepository cityRepo, CompanyRepository companyRepo, ModelRepository modelRepo) {
        this.cityRepo = cityRepo;
        this.companyRepo = companyRepo;
        this.modelRepo = modelRepo;
    }

    public Vehicle vehicleDtoToVehicle(VehicleDto vehicleDto) {
        Optional<City> city = cityRepo.findByName(vehicleDto.getCity());
        if(city.isEmpty())
            throw new NotFoundException("City with name " + vehicleDto.getCity() + " not found");

        Optional<Company> company = companyRepo.findByName(vehicleDto.getCompany());
        if(company.isEmpty())
            throw new NotFoundException("Company with name " + vehicleDto.getCompany() + " not found");
        Optional<Model> model = modelRepo.findByModel(vehicleDto.getModel());
        if(model.isEmpty())
            throw new NotFoundException("Model with name " + vehicleDto.getModel() + " not found");

        Vehicle vehicle = new Vehicle();
        vehicle.setCity(city.get());
        vehicle.setCompany(company.get());
        vehicle.setModel(model.get());
        vehicle.setPlateNumber(vehicleDto.getPlateNumber());
        vehicle.setPricePerDay(vehicleDto.getPricePerDay());

        return vehicle;
    }
}
