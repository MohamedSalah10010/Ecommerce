package com.learn.ecommerce.DTO.Order;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderStatusDTO {
    private Long orderId;
    private String statusMessage;
    private List<OrderItemDTO> items;
    private Double totalPrice;
}
