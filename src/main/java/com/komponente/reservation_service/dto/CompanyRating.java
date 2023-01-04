package com.komponente.reservation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompanyRating {
    private String companyName;
    private double rating;
}
