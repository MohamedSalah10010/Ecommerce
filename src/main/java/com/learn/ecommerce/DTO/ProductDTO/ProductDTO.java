package com.learn.ecommerce.DTO.ProductDTO;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ProductDTO {

    private String name;

    private Double price;

    private String shortDescription;

    private String longDescription;

    private Integer quantity;

    private Long categoryId;

    private String categoryName;
}
