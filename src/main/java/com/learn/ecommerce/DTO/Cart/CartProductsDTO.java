package com.learn.ecommerce.DTO.Cart;

import com.learn.ecommerce.DTO.ProductDTO.ProductStatusDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CartProductsDTO {
    private Long categoryId;
    private String categoryName;
    private List<ProductStatusDTO> Products;
}
