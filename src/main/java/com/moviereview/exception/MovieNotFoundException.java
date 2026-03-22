package com.moviereview.exception;

//─── Movie Not Found ──────────────────────────────────────────
public class MovieNotFoundException extends RuntimeException {
 public MovieNotFoundException(Long movieId) {
     super("Movie not found with ID: " + movieId);
 }
}