package com.komponente.reservation_service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class ReservationCreateDto {
    @NotBlank
    private String plateNumber;
    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;
}
