package com.moviereview.controller;

import com.moviereview.dto.ReviewRequestDTO;
import com.moviereview.dto.ReviewResponseDTO;
import com.moviereview.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * POST /reviews/{movieId}
     * Post a review for a specific movie.
     *
     * Request Body:
     * {
     *   "rating": 8,
     *   "comment": "Amazing film!"
     * }
     */
    @PostMapping("/{movieId}")
    public ResponseEntity<ReviewResponseDTO> addReview(
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewRequestDTO request) {

        ReviewResponseDTO response = reviewService.addReview(movieId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /reviews/{movieId}
     * Get all reviews for a specific movie.
     */
    @GetMapping("/{movieId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByMovie(
            @PathVariable Long movieId) {

        return ResponseEntity.ok(reviewService.getReviewsByMovieId(movieId));
    }

    /**
     * DELETE /reviews/{reviewId}
     * Delete a specific review by its ID.
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();   // 204 No Content
    }
}