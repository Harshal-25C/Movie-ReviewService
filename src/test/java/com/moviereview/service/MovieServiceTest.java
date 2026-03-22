package com.moviereview.service;

import com.moviereview.dto.MovieRequestDTO;
import com.moviereview.dto.MovieResponseDTO;
import com.moviereview.exception.MovieNotFoundException;
import com.moviereview.model.Movie;
import com.moviereview.repository.MovieRepository;
import com.moviereview.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovieService Tests")
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie sampleMovie;

    @BeforeEach
    void setUp() {
        sampleMovie = Movie.builder()
                .id(1L)
                .name("Inception")
                .genre("Sci-Fi")
                .build();
    }

    // ─── addMovie ─────────────────────────────────────────────

    @Test
    @DisplayName("Should add a movie and return response DTO")
    void addMovie_ShouldReturnResponseDTO() {
        MovieRequestDTO request = new MovieRequestDTO("Inception", "Sci-Fi");

        when(movieRepository.save(any(Movie.class))).thenReturn(sampleMovie);
        when(reviewRepository.countByMovieId(1L)).thenReturn(0L);
        when(reviewRepository.findAverageRatingByMovieId(1L)).thenReturn(null);

        MovieResponseDTO result = movieService.addMovie(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Inception");
        assertThat(result.getGenre()).isEqualTo("Sci-Fi");
        assertThat(result.getTotalReviews()).isEqualTo(0);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    // ─── getAllMovies ─────────────────────────────────────────

    @Test
    @DisplayName("Should return all movies as response DTOs")
    void getAllMovies_ShouldReturnList() {
        Movie movie2 = Movie.builder().id(2L).name("Interstellar").genre("Sci-Fi").build();

        when(movieRepository.findAll()).thenReturn(List.of(sampleMovie, movie2));
        when(reviewRepository.countByMovieId(anyLong())).thenReturn(2L);
        when(reviewRepository.findAverageRatingByMovieId(anyLong())).thenReturn(8.5);

        List<MovieResponseDTO> results = movieService.getAllMovies();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("Inception");
        assertThat(results.get(1).getName()).isEqualTo("Interstellar");
    }

    @Test
    @DisplayName("Should return empty list when no movies exist")
    void getAllMovies_WhenEmpty_ShouldReturnEmptyList() {
        when(movieRepository.findAll()).thenReturn(List.of());
        assertThat(movieService.getAllMovies()).isEmpty();
    }

    // ─── getMovieEntityById ───────────────────────────────────

    @Test
    @DisplayName("Should return movie entity when found")
    void getMovieEntityById_WhenFound_ShouldReturnMovie() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleMovie));
        Movie result = movieService.getMovieEntityById(1L);
        assertThat(result.getName()).isEqualTo("Inception");
    }

    @Test
    @DisplayName("Should throw MovieNotFoundException when movie not found")
    void getMovieEntityById_WhenNotFound_ShouldThrow() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> movieService.getMovieEntityById(99L))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("99");
    }
}
