package com.zamrad.service.review;

import com.zamrad.domain.profiles.Profile;
import com.zamrad.domain.Review;
import com.zamrad.dto.profiles.NewReviewDto;
import com.zamrad.repository.ProfileRepository;
import com.zamrad.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProfileRepository profileRepository;

    public Review getReview(UUID reviewId) {
        final Optional<Review> review = Optional.ofNullable(reviewRepository.getOne(reviewId));
        return review.orElseThrow(ReviewNotFoundException::new);
    }

    public List<Review> getAll(UUID artistId) {
        return reviewRepository.getAllReviews(artistId);
    }

    public Review createReview(UUID artistId, NewReviewDto newReviewDto) {
        final Profile profile = profileRepository.getOne(artistId);
        final Review review = Review.builder()
                .profile(profile)
                .content(newReviewDto.getContent())
                .rating(Double.valueOf(newReviewDto.getRating()))
                .title(newReviewDto.getTitle())
                .build();

        return reviewRepository.save(review);
    }

    public void deleteReview(UUID reviewId) {
        final Optional<Review> review = Optional.ofNullable(reviewRepository.getOne(reviewId));
        reviewRepository.delete(review.orElseThrow(ReviewNotFoundException::new));
    }
}
