package com.komponente.reservation_service.controller;

import com.komponente.reservation_service.dto.CompanyRating;
import com.komponente.reservation_service.dto.ReviewCreateDto;
import com.komponente.reservation_service.dto.ReviewDto;
import com.komponente.reservation_service.model.Review;
import com.komponente.reservation_service.service.ReviewService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/review")
public class ReviewController {
    private ReviewService reviewService;

//    make it in a way that client can only add review if he has reservation for that car
    @PostMapping("/create")
    public ResponseEntity<ReviewDto> createReview(@RequestBody @Valid ReviewCreateDto reviewDto) {
        return new ResponseEntity<>(reviewService.createReview(reviewDto), HttpStatus.OK);
    }

//  update functions in a way that in a dto u have to send old username and vehiclePlateNumber <but new rating and comment>
    @PostMapping("/update")
    public ResponseEntity<?> updateReview(@RequestBody @Valid ReviewDto reviewDto) {
        reviewService.updateReview(reviewDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteReview(@RequestParam Long userId, @RequestParam String vehiclePlateNumber) {
        reviewService.deleteReview(userId, vehiclePlateNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    city and company are optional (empty string if not specified)
    @GetMapping("/filtered_reviews")
    public ResponseEntity<List<ReviewDto>> filteredReviews(@RequestParam String city, @RequestParam String company) {
        return new ResponseEntity<>(reviewService.filteredReviews(city, company), HttpStatus.OK);
    }

    @GetMapping("/company_ratings")
    public ResponseEntity<List<CompanyRating>> companyRatings() {
        return new ResponseEntity<>(reviewService.companyRatings(), HttpStatus.OK);
    }

}
