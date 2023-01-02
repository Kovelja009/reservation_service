package com.komponente.reservation_service.controller;

import com.komponente.reservation_service.dto.ModelDto;
import com.komponente.reservation_service.dto.TypeDto;
import com.komponente.reservation_service.dto.VehicleDto;
import com.komponente.reservation_service.service.ModelService;
import com.komponente.reservation_service.service.TypeService;
import com.komponente.reservation_service.service.VehicleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/vehicle")
public class VehicleController {
    private VehicleService vehicleService;
    private TypeService typeService;
    private ModelService modelService;

    @PostMapping("/add_vehicle")
    public ResponseEntity<VehicleDto> addVehicle(@RequestBody @Valid VehicleDto vehicleDto) {
        return new ResponseEntity<>(vehicleService.addVehicle(vehicleDto), HttpStatus.OK);
    }

    @PostMapping("delete_vehicle")
    public ResponseEntity<String> deleteVehicle(@RequestBody String plateNumber) {
        vehicleService.deleteVehicle(plateNumber);
        return new ResponseEntity<>("Vehicle with plate number " + plateNumber + " deleted", HttpStatus.OK);
    }

    @PostMapping("modify_price")
    public ResponseEntity<String> modifyPrice(@RequestParam @Valid String model, @RequestParam @Valid String company, @RequestParam @Valid int newPrice) {
        return new ResponseEntity<>(vehicleService.changePriceForModel(model, company, newPrice), HttpStatus.OK);
    }

    @PostMapping("/add_type")
    public ResponseEntity<TypeDto> addType(@RequestBody @Valid TypeDto typeDto) {
        return new ResponseEntity<>(typeService.addType(typeDto), HttpStatus.OK);
    }

    @PostMapping("/add_model")
    public ResponseEntity<ModelDto> addModel(@RequestBody @Valid ModelDto modelDto) {
        return new ResponseEntity<>(modelService.addModel(modelDto), HttpStatus.OK);
    }
}
