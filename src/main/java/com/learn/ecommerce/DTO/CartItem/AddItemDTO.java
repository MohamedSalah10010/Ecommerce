package com.learn.ecommerce.DTO.CartItem;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AddItemDTO {


    private Long productId;

    private int quantity;
}
