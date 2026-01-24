package com.learn.ecommerce.DTO.Cart;

import com.learn.ecommerce.DTO.CartItem.ItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CartStatusDTO {
    private Long id;
    private List<ItemDTO> items;
    private String statusMessage;
}