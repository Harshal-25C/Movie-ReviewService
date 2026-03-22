package com.moviereview.repository;

import com.moviereview.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Find movies by genre (case-insensitive)
    List<Movie> findByGenreIgnoreCase(String genre);

    // Check if a movie with the same name exists
    boolean existsByNameIgnoreCase(String name);
}