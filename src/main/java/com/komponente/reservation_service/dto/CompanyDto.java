package com.komponente.reservation_service.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDto {
    @NotBlank
    private String name;
    @NotBlank
    private String info;
}
