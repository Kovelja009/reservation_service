package com.komponente.reservation_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class ReservationDto {
    @NotBlank
    private String plateNumber;
    @NotBlank
    private String clientUsername;
    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;
}
