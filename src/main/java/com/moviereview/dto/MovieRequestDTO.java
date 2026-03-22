package com.moviereview.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieRequestDTO {

    @NotBlank(message = "Movie name is required")
    private String name;

    @NotBlank(message = "Genre is required")
    private String genre;
}