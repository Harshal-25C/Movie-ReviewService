package com.moviereview.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {

    private Long reviewId;
    private Long movieId;
    private String movieName;
    private Integer rating;
    private String comment;
}
