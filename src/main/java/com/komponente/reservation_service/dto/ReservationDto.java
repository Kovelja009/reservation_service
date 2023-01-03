package com.komponente.reservation_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class ReservationDto {
    private String plateNumber;
    private Date startDate;
    private Date endDate;
    private int price;
}
