package com.learn.ecommerce.DTO.ProductDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AddProductDTO {
    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Double price;
    @NotBlank
    private String shortDescription;

    private String longDescription;
    @NotNull
    @PositiveOrZero
    private Integer quantity;
    private Long categoryId;
}