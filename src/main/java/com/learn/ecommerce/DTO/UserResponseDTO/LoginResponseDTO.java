package com.learn.ecommerce.DTO.UserResponseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Login response containing JWT token and login status")
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class LoginResponseDTO {

    @Schema(
            description = "JWT access token to be used for authenticated requests",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIn0.signature"
    )
    private String jwt;

    @Schema(
            description = "Indicates whether login was successful",
            example = "true"
    )
    private boolean success;

    @Schema(
            description = "Failure message in case login fails",
            example = "Invalid username or password",
            nullable = true
    )
    private String failureMessage;
}
