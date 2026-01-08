package com.learn.ecommerce.DTO.ProductDTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusDTO {
    private  Long productId;
    private String productName;
    private String statusMessage;
}
