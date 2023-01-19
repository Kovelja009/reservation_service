package com.komponente.reservation_service.controller;

import com.komponente.reservation_service.security.CheckSecurity;
import com.komponente.reservation_service.service.CityService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/city")
public class CityController {
    private CityService cityService;

    @PostMapping("/add_city")
    @CheckSecurity(roles = {"ROLE_ADMIN"})
    public ResponseEntity<String> addCity(@RequestHeader("Authorization") String authorization, @RequestParam @Valid String name) {
        return new ResponseEntity<>(cityService.addCity(name), HttpStatus.OK);
    }
}
