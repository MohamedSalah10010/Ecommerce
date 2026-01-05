package com.learn.ecommerce.DTO.ProductResponseDTO;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class addProductDTO {
    private String name;

    private Double price;

    private String shortDescription;

    private String longDescription;

    private Integer quantity;
}
