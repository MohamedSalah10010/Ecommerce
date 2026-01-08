package com.learn.ecommerce.DTO.CategoryDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AddCategoryDTO {

    @NotBlank
    private String name;
    @NotEmpty
    private String description;
}
