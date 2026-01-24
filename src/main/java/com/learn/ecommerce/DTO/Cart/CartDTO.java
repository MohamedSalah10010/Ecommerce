package com.learn.ecommerce.DTO.Cart;


import com.learn.ecommerce.DTO.CartItem.ItemDTO;
import com.learn.ecommerce.enums.CartStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private Long id;
    private CartStatus status; // ACTIVE, CHECKED_OUT, CANCELLED

    private List<ItemDTO> items;

    private double totalPrice;

}