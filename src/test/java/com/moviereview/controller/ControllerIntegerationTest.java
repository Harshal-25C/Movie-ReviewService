package com.moviereview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviereview.dto.MovieRequestDTO;
import com.moviereview.dto.MovieResponseDTO;
import com.moviereview.dto.ReviewRequestDTO;
import com.moviereview.dto.ReviewResponseDTO;
import com.moviereview.service.MovieService;
import com.moviereview.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {MovieController.class, ReviewController.class})
@DisplayName("Controller Integration Tests")
class ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    @MockBean
    private ReviewService reviewService;

    // ══════════════════════════════════════════════════════════
    // Movie Controller Tests
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /movies → 201 Created")
    void postMovie_ShouldReturn201() throws Exception {
        MovieRequestDTO request = new MovieRequestDTO("Inception", "Sci-Fi");
        MovieResponseDTO response = MovieResponseDTO.builder()
                .id(1L).name("Inception").genre("Sci-Fi")
                .totalReviews(0).averageRating(null).build();

        when(movieService.addMovie(any())).thenReturn(response);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Inception"))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"));
    }

    @Test
    @DisplayName("POST /movies with missing name → 400 Bad Request")
    void postMovie_MissingName_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"genre\": \"Sci-Fi\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.name").exists());
    }

    @Test
    @DisplayName("GET /movies → 200 OK with list")
    void getMovies_ShouldReturn200() throws Exception {
        List<MovieResponseDTO> movies = List.of(
                MovieResponseDTO.builder().id(1L).name("Inception").genre("Sci-Fi")
                        .totalReviews(2).averageRating(8.5).build()
        );

        when(movieService.getAllMovies()).thenReturn(movies);

        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].averageRating").value(8.5));
    }

    // ══════════════════════════════════════════════════════════
    // Review Controller Tests
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /reviews/{movieId} → 201 Created")
    void postReview_ShouldReturn201() throws Exception {
        ReviewRequestDTO request = new ReviewRequestDTO(9, "Brilliant!");
        ReviewResponseDTO response = ReviewResponseDTO.builder()
                .reviewId(10L).movieId(1L).movieName("Inception")
                .rating(9).comment("Brilliant!").build();

        when(reviewService.addReview(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId").value(10))
                .andExpect(jsonPath("$.rating").value(9))
                .andExpect(jsonPath("$.movieName").value("Inception"));
    }

    @Test
    @DisplayName("POST /reviews/{movieId} with invalid rating → 400")
    void postReview_InvalidRating_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\": 15, \"comment\": \"Good\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.rating").exists());
    }

    @Test
    @DisplayName("GET /reviews/{movieId} → 200 with reviews")
    void getReviews_ShouldReturn200() throws Exception {
        List<ReviewResponseDTO> reviews = List.of(
                ReviewResponseDTO.builder().reviewId(1L).movieId(1L)
                        .movieName("Inception").rating(9).comment("Brilliant!").build()
        );
        when(reviewService.getReviewsByMovieId(1L)).thenReturn(reviews);

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].comment").value("Brilliant!"));
    }

    @Test
    @DisplayName("DELETE /reviews/{reviewId} → 204 No Content")
    void deleteReview_ShouldReturn204() throws Exception {
        doNothing().when(reviewService).deleteReview(10L);

        mockMvc.perform(delete("/reviews/10"))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(10L);
    }
}

