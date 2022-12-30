package com.komponente.reservation_service.mapper;

import com.komponente.reservation_service.dto.ReviewDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.model.Review;
import com.komponente.reservation_service.model.Vehicle;
import com.komponente.reservation_service.repository.VehicleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReviewMapper {
    private VehicleRepository vehicleRepo;

    public ReviewMapper(VehicleRepository vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
    }

    public Review reviewDtoToReview(ReviewDto reviewDto) {
        Optional<Vehicle> vehicle = vehicleRepo.findByPlateNumber(reviewDto.getVehiclePlateNumber());
        if(vehicle.isEmpty())
            throw new NotFoundException("Vehicle with plate number " + reviewDto.getVehiclePlateNumber() + " not found");
        Review review = new Review();
        review.setVehicle(vehicle.get());
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());

        return review;
    }
}
