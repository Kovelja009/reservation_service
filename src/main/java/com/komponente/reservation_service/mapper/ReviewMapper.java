package com.komponente.reservation_service.mapper;

import com.komponente.reservation_service.dto.ReviewCreateDto;
import com.komponente.reservation_service.dto.ReviewDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.model.Review;
import com.komponente.reservation_service.model.Vehicle;
import com.komponente.reservation_service.repository.VehicleRepository;
import com.komponente.reservation_service.service.impl.RetryPatternHelper;
import com.komponente.reservation_service.user_sync_comm.dto.UserDto;
import io.github.resilience4j.retry.Retry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@AllArgsConstructor
@Component
public class ReviewMapper {
    private VehicleRepository vehicleRepo;
    private RestTemplate userServiceRestTemplate;
    private Retry serviceRetry;
    private RetryPatternHelper retryPatternHelper;

    public Review reviewCreateDtoToReview(Long userId, ReviewCreateDto reviewDto) {
        Optional<Vehicle> vehicle = vehicleRepo.findByPlateNumber(reviewDto.getVehiclePlateNumber());
        if(!vehicle.isPresent())
            throw new NotFoundException("Vehicle with plate number " + reviewDto.getVehiclePlateNumber() + " not found");
        Review review = new Review();
        review.setVehicle(vehicle.get());
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setUser_id(userId);

        return review;
    }

    public ReviewDto reviewToReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setVehiclePlateNumber(review.getVehicle().getPlateNumber());
        reviewDto.setRating(review.getRating());
        reviewDto.setComment(review.getComment());
        UserDto userDto = retryPatternHelper.getUserByRetry(review.getUser_id(), userServiceRestTemplate);
        if(userDto == null)
            throw new NotFoundException("User with id " + review.getUser_id() + " not found");
        reviewDto.setUsername(userDto.getUsername());

        return reviewDto;
    }

    public ReviewDto reviewCreateDtoToReviewDto(ReviewCreateDto reviewDto) {
        ReviewDto review = new ReviewDto();
        review.setVehiclePlateNumber(reviewDto.getVehiclePlateNumber());
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        return review;
    }
}
