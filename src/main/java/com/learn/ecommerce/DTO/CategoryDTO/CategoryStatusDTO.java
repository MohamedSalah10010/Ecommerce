package com.learn.ecommerce.DTO.CategoryDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CategoryStatusDTO {
    private Long id;
    private String name;
    private String statusMessage;
}
