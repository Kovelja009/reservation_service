package com.komponente.reservation_service.service;

import com.komponente.reservation_service.dto.CompanyRating;
import com.komponente.reservation_service.dto.ReviewCreateDto;
import com.komponente.reservation_service.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto createReview(Long userId, ReviewCreateDto reviewDto);
    void deleteReview(long userId, String vehiclePlateNumber);
    void updateReview(ReviewDto reviewDto);
    List<ReviewDto> filteredReviews(String city, String company);
    List<CompanyRating> companyRatings();
}
