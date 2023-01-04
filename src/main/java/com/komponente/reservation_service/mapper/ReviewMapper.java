package com.komponente.reservation_service.mapper;

import com.komponente.reservation_service.dto.ReviewCreateDto;
import com.komponente.reservation_service.dto.ReviewDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.model.Review;
import com.komponente.reservation_service.model.Vehicle;
import com.komponente.reservation_service.repository.VehicleRepository;
import com.komponente.reservation_service.user_sync_comm.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@AllArgsConstructor
@Component
public class ReviewMapper {
    private VehicleRepository vehicleRepo;
    private RestTemplate userServiceRestTemplate;
    public Review reviewCreateDtoToReview(ReviewCreateDto reviewDto) {
        Optional<Vehicle> vehicle = vehicleRepo.findByPlateNumber(reviewDto.getVehiclePlateNumber());
        if(vehicle.isEmpty())
            throw new NotFoundException("Vehicle with plate number " + reviewDto.getVehiclePlateNumber() + " not found");
        Review review = new Review();
        review.setVehicle(vehicle.get());
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setUser_id(reviewDto.getUserId());

        return review;
    }

    public ReviewDto reviewToReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setVehiclePlateNumber(review.getVehicle().getPlateNumber());
        reviewDto.setRating(review.getRating());
        reviewDto.setComment(review.getComment());
        ResponseEntity<UserDto> userDto =  userServiceRestTemplate.exchange("/user/id?id="+review.getUser_id().toString(), HttpMethod.GET, null, UserDto.class);
        if(userDto.getBody() == null)
            throw new NotFoundException("User with id " + review.getUser_id() + " not found");
        reviewDto.setUsername(userDto.getBody().getUsername());

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
