package com.learn.ecommerce.DTO.UserRequestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Login request payload")
@Data
@Builder
@AllArgsConstructor

@NoArgsConstructor
public class LoginBodyDTO {

    @NotEmpty
    @Schema(
            description = "Username",
            example = "john_doe",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @NotEmpty
    @Schema(
            description = "User password",
            example = "Password123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}