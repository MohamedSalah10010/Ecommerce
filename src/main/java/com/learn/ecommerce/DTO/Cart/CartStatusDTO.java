package com.learn.ecommerce.DTO.Cart;

import com.learn.ecommerce.DTO.CartItem.ItemDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CartStatusDTO {
    private Long id;
    private List<ItemDTO> items;
    private String statusMessage;
}
