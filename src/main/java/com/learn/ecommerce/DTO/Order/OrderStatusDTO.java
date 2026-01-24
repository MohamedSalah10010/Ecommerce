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

public class OrderStatusDTO {
    private Long orderId;
    private String statusMessage;
    private List<OrderItemDTO> items;
    private Double totalPrice;
}