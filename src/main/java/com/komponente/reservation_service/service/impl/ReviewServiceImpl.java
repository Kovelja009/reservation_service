package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.dto.CompanyRating;
import com.komponente.reservation_service.dto.ReviewCreateDto;
import com.komponente.reservation_service.dto.ReviewDto;
import com.komponente.reservation_service.mapper.ReviewMapper;
import com.komponente.reservation_service.model.Review;
import com.komponente.reservation_service.repository.ReviewRepository;
import com.komponente.reservation_service.service.ReviewService;
import com.komponente.reservation_service.user_sync_comm.dto.UserDto;
import com.komponente.reservation_service.user_sync_comm.dto.UserIdDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private ReviewRepository reviewRepo;
    private ReviewMapper reviewMapper;
    private RestTemplate userServiceRestTemplate;

    @Override
    public ReviewDto createReview(ReviewCreateDto reviewCreateDto) {
        Review review = reviewMapper.reviewCreateDtoToReview(reviewCreateDto);

        ResponseEntity<UserDto> userDto =  userServiceRestTemplate.exchange("/user/id?id="+reviewCreateDto.getUserId().toString(), HttpMethod.GET, null, UserDto.class);
        if (reviewRepo.findReviewForDeleting(review.getVehicle().getPlateNumber(), review.getUser_id()).isPresent())
            throw new IllegalArgumentException("User with username " + userDto.getBody().getUsername() + " has already reviewed vehicle with plate number " + review.getVehicle().getPlateNumber());
        reviewRepo.save(review);

        ReviewDto reviewDto = reviewMapper.reviewCreateDtoToReviewDto(reviewCreateDto);
        reviewDto.setUsername(userDto.getBody().getUsername());

        return reviewDto;
    }

    @Override
    public void deleteReview(long userId, String vehiclePlateNumber) {
        Optional<Review> review = reviewRepo.findReviewForDeleting(vehiclePlateNumber, userId);
        if (review.isEmpty())
            throw new IllegalArgumentException("Review for vehicle with plate number " + vehiclePlateNumber + " and user with id " + userId + " not found");
        reviewRepo.delete(review.get());
    }

    @Override
    public void updateReview(ReviewDto reviewDto) {
        ResponseEntity<UserIdDto> userId =  userServiceRestTemplate.exchange("/user/username?username="+reviewDto.getUsername(), HttpMethod.GET, null, UserIdDto.class);
        Optional<Review> review = reviewRepo.findReviewForDeleting(reviewDto.getVehiclePlateNumber(), userId.getBody().getId());
        if (review.isEmpty())
            throw new IllegalArgumentException("Review for vehicle with plate number " + reviewDto.getVehiclePlateNumber() + " and user with id " + userId.getBody().getId() + " not found");
        review.get().setComment(reviewDto.getComment());
        review.get().setRating(reviewDto.getRating());
        reviewRepo.save(review.get());
    }

    @Override
    public List<ReviewDto> filteredReviews(String city, String company) {
        int query = VehicleServiceImpl.getRightQuery(city, company);

        Optional<List<Review>> reviewOptional = null;

        switch (query) {
            case 1 -> reviewOptional = reviewRepo.findByCityAndCompany(city, company);
            case 2 -> reviewOptional = reviewRepo.findByCity(city);
            case 3 -> reviewOptional = reviewRepo.findByCompany(company);
            case 4 -> reviewOptional = reviewRepo.findAllReviews();
        }

        if(reviewOptional.isEmpty() || reviewOptional.get().isEmpty())
            throw new IllegalArgumentException("No reviews found");

        List<ReviewDto> reviews = reviewOptional.get().stream().map(reviewMapper::reviewToReviewDto).toList();

        return reviews;
    }

    @Override
    public List<CompanyRating> companyRatings() {
        if(reviewRepo.findCompanyRating().isEmpty())
            throw new IllegalArgumentException("No reviews found");
        return reviewRepo.findCompanyRating().get();
    }
}
