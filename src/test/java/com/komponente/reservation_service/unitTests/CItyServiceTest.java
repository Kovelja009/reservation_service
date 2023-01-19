package com.komponente.reservation_service.unitTests;

import com.komponente.reservation_service.model.City;
import com.komponente.reservation_service.repository.CityRepository;
import com.komponente.reservation_service.service.CityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CItyServiceTest {

    @MockBean
    private CityRepository cityRepo;

    @Autowired
    private CityService cityService;

    @Test
    public void addCityAlreadyExistsTest(){
        City city = new City();
        city.setCity("Tokyo");

        when(cityRepo.findByCity("Tokyo")).thenReturn(Optional.of(city));

        Exception e = assertThrows(IllegalArgumentException.class, () -> cityService.addCity("Tokyo"));
        assertEquals("City Tokyo already exists", e.getMessage());

        verify(cityRepo).findByCity("Tokyo");
        verify(cityRepo, never()).save(city);
    }

    @Test
    public void addCityTest(){
        when(cityRepo.findByCity("Tokyo")).thenReturn(Optional.empty());

        assertEquals("Tokyo", cityService.addCity("Tokyo"));

        verify(cityRepo).findByCity("Tokyo");
        verify(cityRepo).save(any());
    }

}
