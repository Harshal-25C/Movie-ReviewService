package com.moviereview.service;

import com.moviereview.dto.ReviewRequestDTO;
import com.moviereview.dto.ReviewResponseDTO;
import com.moviereview.exception.ReviewNotFoundException;
import com.moviereview.model.Movie;
import com.moviereview.model.Review;
import com.moviereview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieService movieService;

    // ─── Post a review for a movie ────────────────────────────
    public ReviewResponseDTO addReview(Long movieId, ReviewRequestDTO request) {
        // Validate movie exists first
        Movie movie = movieService.getMovieEntityById(movieId);

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .movie(movie)
                .build();

        Review saved = reviewRepository.save(review);
        return toResponseDTO(saved);
    }

    // ─── Get all reviews for a movie ─────────────────────────
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByMovieId(Long movieId) {
        // Validate movie exists
        movieService.getMovieEntityById(movieId);

        return reviewRepository.findByMovieId(movieId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── Delete a review by reviewId ─────────────────────────
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        reviewRepository.delete(review);
    }

    // ─── Mapper: Review → ReviewResponseDTO ──────────────────
    private ReviewResponseDTO toResponseDTO(Review review) {
        return ReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .movieId(review.getMovie().getId())
                .movieName(review.getMovie().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }
}
