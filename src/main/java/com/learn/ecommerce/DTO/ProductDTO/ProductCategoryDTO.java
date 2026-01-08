package com.learn.ecommerce.DTO.ProductDTO;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ProductCategoryDTO {
     private Long     productId;
     private String   productName;
     private Long     categoryId;
     private String   categoryName;


}
