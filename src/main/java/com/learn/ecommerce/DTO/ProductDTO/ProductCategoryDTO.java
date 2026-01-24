package com.learn.ecommerce.DTO.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryDTO {
     private Long     productId;
     private String   productName;
     private Long     categoryId;
     private String   categoryName;


}