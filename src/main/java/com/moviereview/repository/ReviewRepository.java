package com.moviereview.repository;

import com.moviereview.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Get all reviews for a specific movie
    List<Review> findByMovieId(Long movieId);

    // Count reviews for a movie
    long countByMovieId(Long movieId);

    // Calculate average rating for a movie
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie.id = :movieId")
    Double findAverageRatingByMovieId(@Param("movieId") Long movieId);

    // Delete all reviews belonging to a movie
    void deleteByMovieId(Long movieId);
}
