package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.dto.VehicleDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.mapper.VehicleMapper;
import com.komponente.reservation_service.model.Company;
import com.komponente.reservation_service.model.Model;
import com.komponente.reservation_service.model.Reservation;
import com.komponente.reservation_service.model.Vehicle;
import com.komponente.reservation_service.repository.CompanyRepository;
import com.komponente.reservation_service.repository.ModelRepository;
import com.komponente.reservation_service.repository.ReservationRepository;
import com.komponente.reservation_service.repository.VehicleRepository;
import com.komponente.reservation_service.service.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class VehicleServiceImpl implements VehicleService {
    private VehicleRepository vehicleRepo;
    private VehicleMapper vehicleMapper;
    private ModelRepository modelRepo;
    private CompanyRepository companyRepo;
    private ReservationRepository reservationRepo;

    @Override
    public VehicleDto addVehicle(VehicleDto vehicleDto) {
        Optional<Vehicle> optional = vehicleRepo.findByPlateNumber(vehicleDto.getPlateNumber());
        if (optional.isPresent())
            throw new IllegalStateException("Vehicle with plate number " + vehicleDto.getPlateNumber() + " already exists");

        Vehicle vehicle = vehicleMapper.vehicleDtoToVehicle(vehicleDto);
        vehicleRepo.save(vehicle);
        return vehicleDto;
    }

    @Override
    public void deleteVehicle(String plateNumber) {
        Optional<Vehicle> optional = vehicleRepo.findByPlateNumber(plateNumber);
        if (optional.isPresent())
            vehicleRepo.delete(optional.get());
        else
            throw new IllegalStateException("Vehicle with plate number " + plateNumber + " does not exist");
    }


    @Override
    public String changePriceForModel(String model, String company, int newPrice) {
        Model mdl = modelRepo.findByModel(model).get();
        Company cmp = companyRepo.findByName(company).get();
        Optional<List<Vehicle>> optional = vehicleRepo.findByModelAndAndCompany(mdl, cmp);
        if(optional.isPresent()) {
            List<Vehicle> vehicles = optional.get();
            for (Vehicle vehicle : vehicles) {
                vehicle.setPricePerDay(newPrice);
                vehicleRepo.save(vehicle);
            }
            return "Price for model " + model + " changed to " + newPrice;
        } else {
            throw new IllegalStateException("Model " + model + " does not exist");
        }
    }

    @Override
    public List<VehicleDto> getAllAvailableVehicles(String city, String company, Date startDate, Date endDate) {
        int query = getRightQuery(city, company);
        Optional<List<Vehicle>> vehicleOptional = null;

        switch (query) {
            case 1 :
                vehicleOptional = vehicleRepo.findByCityAndCompany(city, company);
                break;
            case 2 :
                vehicleOptional = vehicleRepo.findByCity(city);
                break;
            case 3 :
                vehicleOptional = vehicleRepo.findByCompany(company);
                break;
            case 4 :
                vehicleOptional = vehicleRepo.findAllAvailableVehicles();
        }

        if(!vehicleOptional.isPresent() || vehicleOptional.get().isEmpty())
            throw new NotFoundException("No vehicles found");

        List<Vehicle> vehiclesList = vehicleOptional.get();


        List<VehicleDto> vehicles = new ArrayList<>();

        for(Vehicle vehicle : vehiclesList) {
            Optional<List<Reservation>> reservationsOptional = reservationRepo.findByVehicle(vehicle);
            if(!vehicleOptional.isPresent())
                vehicles.add(vehicleMapper.vehicleToVehicleDto(vehicle));
            else{
                List<Reservation> reservations = reservationsOptional.get();
                boolean available = true;
                for(Reservation reservation : reservations) {
                    if(startDate.after(reservation.getEndDate()) || endDate.before(reservation.getStartDate()))
                        available = true;
                    else {
                        available = false;
                        break;
                    }
                }
                if(available)
                    vehicles.add(vehicleMapper.vehicleToVehicleDto(vehicle));
            }
        }

        return vehicles;
    }

    protected static int getRightQuery(String city, String company) {
        if(!city.equals("") && !company.equals(""))
            return 1;
        else if(!city.equals("") && company.equals(""))
            return 2;
        else if(city.equals("") && !company.equals(""))
            return 3;
        else
            return 4;
    }
}
