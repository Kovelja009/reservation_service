package com.komponente.reservation_service.controller;

import com.komponente.reservation_service.service.CityService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/city")
public class CityController {
    private CityService cityService;

    @PostMapping("/add_city")
    public ResponseEntity<String> addCity(@RequestParam @Valid String name) {
        return new ResponseEntity<>(cityService.addCity(name), HttpStatus.OK);
    }
}
