package com.learn.ecommerce.DTO.Order;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlaceOrderDTO {
    private Long addressId; // shipping address
    private List<OrderItemInputDTO> items; // optional if converting cart items
}
