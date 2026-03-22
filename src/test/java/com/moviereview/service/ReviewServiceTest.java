package com.moviereview.service;

import com.moviereview.dto.ReviewRequestDTO;
import com.moviereview.dto.ReviewResponseDTO;
import com.moviereview.exception.ReviewNotFoundException;
import com.moviereview.model.Movie;
import com.moviereview.model.Review;
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
@DisplayName("ReviewService Tests")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieService movieService;

    @InjectMocks
    private ReviewService reviewService;

    private Movie sampleMovie;
    private Review sampleReview;

    @BeforeEach
    void setUp() {
        sampleMovie = Movie.builder()
                .id(1L).name("Inception").genre("Sci-Fi").build();

        sampleReview = Review.builder()
                .reviewId(10L).rating(9)
                .comment("Brilliant!").movie(sampleMovie).build();
    }

    // ─── addReview ────────────────────────────────────────────

    @Test
    @DisplayName("Should add review and return DTO")
    void addReview_ShouldReturnResponseDTO() {
        ReviewRequestDTO request = new ReviewRequestDTO(9, "Brilliant!");

        when(movieService.getMovieEntityById(1L)).thenReturn(sampleMovie);
        when(reviewRepository.save(any(Review.class))).thenReturn(sampleReview);

        ReviewResponseDTO result = reviewService.addReview(1L, request);

        assertThat(result.getReviewId()).isEqualTo(10L);
        assertThat(result.getMovieId()).isEqualTo(1L);
        assertThat(result.getRating()).isEqualTo(9);
        assertThat(result.getComment()).isEqualTo("Brilliant!");
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw when movie not found during addReview")
    void addReview_WhenMovieNotFound_ShouldPropagate() {
        ReviewRequestDTO request = new ReviewRequestDTO(7, "Good");

        when(movieService.getMovieEntityById(99L))
                .thenThrow(new com.moviereview.exception.MovieNotFoundException(99L));

        assertThatThrownBy(() -> reviewService.addReview(99L, request))
                .isInstanceOf(com.moviereview.exception.MovieNotFoundException.class);
    }

    // ─── getReviewsByMovieId ──────────────────────────────────

    @Test
    @DisplayName("Should return all reviews for a movie")
    void getReviewsByMovieId_ShouldReturnList() {
        Review r2 = Review.builder().reviewId(11L).rating(8)
                .comment("Great!").movie(sampleMovie).build();

        when(movieService.getMovieEntityById(1L)).thenReturn(sampleMovie);
        when(reviewRepository.findByMovieId(1L)).thenReturn(List.of(sampleReview, r2));

        List<ReviewResponseDTO> results = reviewService.getReviewsByMovieId(1L);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getRating()).isEqualTo(9);
        assertThat(results.get(1).getRating()).isEqualTo(8);
    }

    // ─── deleteReview ─────────────────────────────────────────

    @Test
    @DisplayName("Should delete review when found")
    void deleteReview_WhenFound_ShouldDelete() {
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(sampleReview));
        doNothing().when(reviewRepository).delete(sampleReview);

        reviewService.deleteReview(10L);

        verify(reviewRepository, times(1)).delete(sampleReview);
    }

    @Test
    @DisplayName("Should throw ReviewNotFoundException when review not found")
    void deleteReview_WhenNotFound_ShouldThrow() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(999L))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("999");
    }
}

