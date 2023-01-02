package com.komponente.reservation_service.mapper;

import com.komponente.reservation_service.dto.ModelDto;
import com.komponente.reservation_service.dto.TypeDto;
import com.komponente.reservation_service.dto.VehicleDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.model.*;
import com.komponente.reservation_service.repository.CityRepository;
import com.komponente.reservation_service.repository.CompanyRepository;
import com.komponente.reservation_service.repository.ModelRepository;
import com.komponente.reservation_service.repository.TypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
@AllArgsConstructor
@Component
public class VehicleMapper {
    private CityRepository cityRepo;
    private CompanyRepository companyRepo;
    private ModelRepository modelRepo;
    private TypeRepository typeRepo;


    public Vehicle vehicleDtoToVehicle(VehicleDto vehicleDto) {
        Optional<City> city = cityRepo.findByCity(vehicleDto.getCity());
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

    public Model modelDtoToModel(ModelDto modeldto){
        Optional<Type> type = typeRepo.findByType(modeldto.getType());
        if(type.isEmpty())
            throw new NotFoundException("Type " + modeldto.getType() + " not found");
        Model model = new Model();
        model.setModel(modeldto.getModel());
        model.setType(type.get());

        return model;
    }

    public Type typeDtoToType(TypeDto typedto){
        Type type = new Type();
        type.setType(typedto.getType());
        return type;
    }
}
