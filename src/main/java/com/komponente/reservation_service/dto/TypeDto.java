package com.komponente.reservation_service.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeDto {
    @NotBlank
    private String type;
}
