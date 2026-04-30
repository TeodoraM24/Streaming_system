package org.example.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovieResponseDTO {

    private Long movieId;
    private Short duration;

    private String title;
    private BigDecimal rating;
    private String description;
}
