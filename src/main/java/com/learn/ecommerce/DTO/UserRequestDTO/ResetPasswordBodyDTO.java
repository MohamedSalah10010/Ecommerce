package com.learn.ecommerce.DTO.UserRequestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Reset password request payload")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordBodyDTO {

    @NotBlank
    @NotNull
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    @Schema(
            description = "New password (minimum 8 characters, at least one letter and one number)",
            example = "NewPassword123",
            minLength = 8,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String newPassword;

    @NotBlank
    @Schema(
            description = "Password reset token",
            example = "eyJhbGciOiJIUzI1NiJ9.reset.token",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String token;
}