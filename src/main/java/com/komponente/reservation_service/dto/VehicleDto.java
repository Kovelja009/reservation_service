package com.komponente.reservation_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleDto {
    @NotBlank
    private String plateNumber;
    @NotBlank
    private String model;
    @NotBlank
    private String type;
    @NotBlank
    private String company;
    @NotBlank
    private String city;
    @NotNull
    private int pricePerDay;
}
