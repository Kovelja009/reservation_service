package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.model.City;
import com.komponente.reservation_service.repository.CityRepository;
import com.komponente.reservation_service.service.CityService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Getter
@Service
public class CityServiceImpl implements CityService {
    private CityRepository cityRepo;


    @Override
    public String addCity(String name) {
        Optional<City> city = cityRepo.findByCity(name);
        if(city.isPresent())
            throw new IllegalArgumentException("City " + name + " already exists");

        City newCity = new City();
        newCity.setCity(name);
        cityRepo.save(newCity);

        return name;
    }

}
