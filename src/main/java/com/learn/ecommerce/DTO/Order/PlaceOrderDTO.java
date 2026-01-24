package com.learn.ecommerce.DTO.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderDTO {
    private Long addressId; // shipping address
    private List<OrderItemInputDTO> items; // optional if converting cart items
}