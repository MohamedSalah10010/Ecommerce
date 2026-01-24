package com.learn.ecommerce.DTO.CartItem;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {

   private Long id;
   private Long productId;
   private String productName;
   private Integer quantity;
   private Double unitPrice;
   private Double totalPrice;

}