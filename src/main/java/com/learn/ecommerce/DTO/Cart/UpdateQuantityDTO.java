package com.learn.ecommerce.DTO.Cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateQuantityDTO {

    @NotNull
    @Positive
    private Integer quantity;
}
