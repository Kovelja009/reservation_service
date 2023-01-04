package com.komponente.reservation_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateDto {
    @Min(1)
    @Max(5)
    @NotNull
    private int rating;
    @NotBlank
    private String comment;
    @NotBlank
    private String vehiclePlateNumber;
    @NotNull
    private Long userId;
}
