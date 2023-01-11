package com.komponente.reservation_service.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
    private int rating;
    @NotBlank
    private String comment;
    @NotBlank
    private String vehiclePlateNumber;
    @NotBlank
    private String username;
}
