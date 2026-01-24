package com.learn.ecommerce.DTO.Category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatusDTO {
    private Long id;
    private String name;
    private String statusMessage;
}