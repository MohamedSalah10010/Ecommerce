package com.learn.ecommerce.DTO.UserRequestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Forgot password request payload")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordBodyDTO {

    @NotEmpty
    @Schema(
            description = "User email address",
            example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotEmpty
    @Schema(
            description = "Username associated with the account",
            example = "john_doe",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;
}