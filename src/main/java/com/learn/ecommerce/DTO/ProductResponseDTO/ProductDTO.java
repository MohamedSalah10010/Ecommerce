package com.learn.ecommerce.DTO.ProductResponseDTO;

import com.learn.ecommerce.entity.Inventory;
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

    private Inventory inventory;
}
