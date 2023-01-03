package com.komponente.reservation_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class ReservationCreateDto {
    @NotBlank
    private String plateNumber;
    @NotBlank
    private long userId;
    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;
}
