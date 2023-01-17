package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.dto.CompanyRating;
import com.komponente.reservation_service.dto.ReviewCreateDto;
import com.komponente.reservation_service.dto.ReviewDto;
import com.komponente.reservation_service.mapper.ReviewMapper;
import com.komponente.reservation_service.model.Review;
import com.komponente.reservation_service.repository.ReviewRepository;
import com.komponente.reservation_service.service.ReviewService;
import com.komponente.reservation_service.user_sync_comm.dto.UserDto;
import io.github.resilience4j.retry.Retry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private ReviewRepository reviewRepo;
    private ReviewMapper reviewMapper;
    private RestTemplate userServiceRestTemplate;
    private Retry serviceRetry;
    private RetryPatternHelper retryPatternHelper;

    @Override
    public ReviewDto createReview(Long userId, ReviewCreateDto reviewCreateDto) {
        Review review = reviewMapper.reviewCreateDtoToReview(userId, reviewCreateDto);

        UserDto userDto = retryPatternHelper.getUserByRetry(review.getUser_id(), userServiceRestTemplate);
        if (reviewRepo.findReviewForDeleting(review.getVehicle().getPlateNumber(), review.getUser_id()).isPresent())
            throw new IllegalArgumentException("User with username " + userDto.getUsername() + " has already reviewed vehicle with plate number " + review.getVehicle().getPlateNumber());
        reviewRepo.save(review);

        ReviewDto reviewDto = reviewMapper.reviewCreateDtoToReviewDto(reviewCreateDto);
        reviewDto.setUsername(userDto.getUsername());

        return reviewDto;
    }

    @Override
    public void deleteReview(long userId, String vehiclePlateNumber) {
        Optional<Review> review = reviewRepo.findReviewForDeleting(vehiclePlateNumber, userId);
        if (!review.isPresent())
            throw new IllegalArgumentException("Review for vehicle with plate number " + vehiclePlateNumber + " and user with id " + userId + " not found");
        reviewRepo.delete(review.get());
    }

    @Override
    public void updateReview(ReviewDto reviewDto) {
        Long userId = retryPatternHelper.getUserIdByRetry(reviewDto.getUsername(), userServiceRestTemplate);
        Optional<Review> review = reviewRepo.findReviewForDeleting(reviewDto.getVehiclePlateNumber(), userId);
        if (!review.isPresent())
            throw new IllegalArgumentException("Review for vehicle with plate number " + reviewDto.getVehiclePlateNumber() + " and user with id " + userId + " not found");
        review.get().setComment(reviewDto.getComment());
        review.get().setRating(reviewDto.getRating());
        reviewRepo.save(review.get());
    }

    @Override
    public List<ReviewDto> filteredReviews(String city, String company) {
        int query = VehicleServiceImpl.getRightQuery(city, company, true);

        Optional<List<Review>> reviewOptional = null;

        switch (query) {
            case 1 :
                reviewOptional = reviewRepo.findByCityAndCompany(city, company);
                break;
            case 2 :
                reviewOptional = reviewRepo.findByCity(city);
                break;
            case 3 :
                reviewOptional = reviewRepo.findByCompany(company);
                break;
            case 4 :
                reviewOptional = reviewRepo.findAllReviews();
        }

        if(!reviewOptional.isPresent() || reviewOptional.get().isEmpty())
            throw new IllegalArgumentException("No reviews found");

        List<ReviewDto> reviews = reviewOptional.get().stream().map(reviewMapper::reviewToReviewDto).collect(Collectors.toList());

        return reviews;
    }

    @Override
    public List<CompanyRating> companyRatings() {
        if(!reviewRepo.findCompanyRating().isPresent())
            throw new IllegalArgumentException("No reviews found");
        return reviewRepo.findCompanyRating().get();
    }

}
