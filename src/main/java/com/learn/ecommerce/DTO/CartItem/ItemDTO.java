package com.learn.ecommerce.DTO.CartItem;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDTO {

   private Long id;
   private Long productId;
   private String productName;
   private Integer quantity;
   private Double unitPrice;
   private Double totalPrice;

}
