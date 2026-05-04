package org.example.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShowResponseDTO {

    private Long showsId;

    private String title;
    private BigDecimal rating;
    private String description;
}
