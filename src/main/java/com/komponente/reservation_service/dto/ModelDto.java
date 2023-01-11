package com.komponente.reservation_service.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelDto {
    @NotBlank
    private String type;
    @NotBlank
    private String model;
}
