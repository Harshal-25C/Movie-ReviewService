package com.moviereview.controller;

import com.moviereview.dto.MovieRequestDTO;
import com.moviereview.dto.MovieResponseDTO;
import com.moviereview.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    /**
     * POST /movies
     * Add a new movie.
     *
     * Request Body:
     * {
     *   "name": "Inception",
     *   "genre": "Sci-Fi"
     * }
     */
    @PostMapping
    public ResponseEntity<MovieResponseDTO> addMovie(
            @Valid @RequestBody MovieRequestDTO request) {

        MovieResponseDTO response = movieService.addMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /movies
     * Retrieve all movies with their review stats.
     */
    @GetMapping
    public ResponseEntity<List<MovieResponseDTO>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }
}
