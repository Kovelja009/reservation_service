package com.komponente.reservation_service.controller;

import com.komponente.reservation_service.dto.ModelDto;
import com.komponente.reservation_service.dto.TypeDto;
import com.komponente.reservation_service.dto.VehicleDto;
import com.komponente.reservation_service.security.CheckSecurity;
import com.komponente.reservation_service.service.ModelService;
import com.komponente.reservation_service.service.TypeService;
import com.komponente.reservation_service.service.VehicleService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/vehicle")
public class VehicleController {
    private VehicleService vehicleService;
    private TypeService typeService;
    private ModelService modelService;

    @PostMapping("/add_vehicle")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<VehicleDto> addVehicle(@RequestHeader("Authorization") String authorization, @RequestBody @Valid VehicleDto vehicleDto) {
        return new ResponseEntity<>(vehicleService.addVehicle(vehicleDto), HttpStatus.OK);
    }

    @PostMapping("delete_vehicle")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<String> deleteVehicle(@RequestHeader("Authorization") String authorization, @RequestBody String plateNumber) {
        vehicleService.deleteVehicle(plateNumber);
        return new ResponseEntity<>("Vehicle with plate number " + plateNumber + " deleted", HttpStatus.OK);
    }

    @PostMapping("modify_price")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<String> modifyPrice(@RequestHeader("Authorization") String authorization, @RequestParam @Valid String model, @RequestParam @Valid String company, @RequestParam @Valid int newPrice) {
        return new ResponseEntity<>(vehicleService.changePriceForModel(model, company, newPrice), HttpStatus.OK);
    }

    @PostMapping("/add_type")
    @CheckSecurity(roles = {"ROLE_ADMIN"})
    public ResponseEntity<TypeDto> addType(@RequestHeader("Authorization") String authorization, @RequestBody @Valid TypeDto typeDto) {
        return new ResponseEntity<>(typeService.addType(typeDto), HttpStatus.OK);
    }

    @PostMapping("/add_model")
    @CheckSecurity(roles = {"ROLE_ADMIN"})
    public ResponseEntity<ModelDto> addModel(@RequestHeader("Authorization") String authorization, @RequestBody @Valid ModelDto modelDto) {
        return new ResponseEntity<>(modelService.addModel(modelDto), HttpStatus.OK);
    }

//    city and company are optional (empty string if not specified) but dates and order must be specified
    @GetMapping("/available_vehicles")
    public ResponseEntity<List<VehicleDto>> getAllAvailableVehicles(@RequestParam String city, @RequestParam String company, @RequestParam Date startDate, @RequestParam Date endDate, @RequestParam boolean asc) {
        return new ResponseEntity<>(vehicleService.getAllAvailableVehicles(city, company, startDate, endDate, asc), HttpStatus.OK);
    }
}
