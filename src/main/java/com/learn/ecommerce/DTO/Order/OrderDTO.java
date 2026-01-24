package com.learn.ecommerce.DTO.Order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class OrderDTO  {
    private Long id;
    private Long userId; // optional, if needed
    private String orderStatus;
    private Double totalPrice;
    private Instant createdAt;
    private Instant updatedAt;

    private List<OrderItemDTO> items;
}