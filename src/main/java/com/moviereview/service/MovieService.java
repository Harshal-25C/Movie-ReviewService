package com.moviereview.service;

import com.moviereview.dto.MovieRequestDTO;
import com.moviereview.dto.MovieResponseDTO;
import com.moviereview.exception.MovieNotFoundException;
import com.moviereview.model.Movie;
import com.moviereview.repository.MovieRepository;
import com.moviereview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    // ─── Add a new movie ──────────────────────────────────────
    public MovieResponseDTO addMovie(MovieRequestDTO request) {
        Movie movie = Movie.builder()
                .name(request.getName())
                .genre(request.getGenre())
                .build();

        Movie saved = movieRepository.save(movie);
        return toResponseDTO(saved);
    }

    // ─── Get all movies ───────────────────────────────────────
    @Transactional(readOnly = true)
    public List<MovieResponseDTO> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── Get a movie by ID (used internally) ─────────────────
    @Transactional(readOnly = true)
    public Movie getMovieEntityById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
    }

    // ─── Mapper: Movie → MovieResponseDTO ────────────────────
    private MovieResponseDTO toResponseDTO(Movie movie) {
        long total = reviewRepository.countByMovieId(movie.getId());
        Double avg = reviewRepository.findAverageRatingByMovieId(movie.getId());

        return MovieResponseDTO.builder()
                .id(movie.getId())
                .name(movie.getName())
                .genre(movie.getGenre())
                .totalReviews((int) total)
                .averageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : null)
                .build();
    }
}
