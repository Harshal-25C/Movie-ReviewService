package com.moviereview.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponseDTO {

    private Long id;
    private String name;
    private String genre;
    private int totalReviews;
    private Double averageRating;
}