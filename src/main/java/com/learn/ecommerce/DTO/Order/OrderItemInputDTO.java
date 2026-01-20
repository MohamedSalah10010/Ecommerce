package com.learn.ecommerce.DTO.Order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemInputDTO {
    private Long productId;
    private Integer quantity;
}
