package com.moviereview.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(Long reviewId) {
        super("Review not found with ID: " + reviewId);
    }
}
