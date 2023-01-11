package com.komponente.reservation_service.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
