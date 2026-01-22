package com.learn.ecommerce.DTO.Cart;


import com.learn.ecommerce.DTO.CartItem.ItemDTO;
import com.learn.ecommerce.enums.CartStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDTO {

    private Long id;
    private CartStatus status; // ACTIVE, CHECKED_OUT, CANCELLED

    private List<ItemDTO> items;

    private double totalPrice;

}
