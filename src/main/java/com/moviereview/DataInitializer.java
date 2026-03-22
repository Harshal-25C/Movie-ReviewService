package com.moviereview;

import com.moviereview.model.Movie;
import com.moviereview.model.Review;
import com.moviereview.repository.MovieRepository;
import com.moviereview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void run(String... args) {
        log.info(">>> Seeding sample data...");

        // ── Movies ────────────────────────────────────────────
        Movie inception = movieRepository.save(
                Movie.builder().name("Inception").genre("Sci-Fi").build());

        Movie interstellar = movieRepository.save(
                Movie.builder().name("Interstellar").genre("Sci-Fi").build());

        Movie darkKnight = movieRepository.save(
                Movie.builder().name("The Dark Knight").genre("Action").build());

        // ── Reviews for Inception ─────────────────────────────
        reviewRepository.save(Review.builder()
                .rating(9).comment("Mind-bending plot, absolutely loved it!").movie(inception).build());
        reviewRepository.save(Review.builder()
                .rating(8).comment("Great film but requires full attention.").movie(inception).build());

        // ── Reviews for Interstellar ──────────────────────────
        reviewRepository.save(Review.builder()
                .rating(10).comment("The most emotional sci-fi I have ever seen.").movie(interstellar).build());
        reviewRepository.save(Review.builder()
                .rating(9).comment("Stunning visuals and an incredible score.").movie(interstellar).build());

        // ── Reviews for The Dark Knight ───────────────────────
        reviewRepository.save(Review.builder()
                .rating(10).comment("Ledger's Joker is legendary. Perfect film.").movie(darkKnight).build());

        log.info(">>> Sample data seeded: {} movies, {} reviews",
                movieRepository.count(), reviewRepository.count());
    }
}