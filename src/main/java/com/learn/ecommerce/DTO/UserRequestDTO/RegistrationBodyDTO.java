package com.learn.ecommerce.DTO.UserRequestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;


@Schema(description = "User registration request payload")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class RegistrationBodyDTO {

    @NotBlank
    @NotNull
    @Size(min = 3)
    @Schema(
            description = "Unique username",
            example = "john_doe",
            minLength = 3,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @NotBlank
    @NotNull
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    @Schema(
            description = "Password (minimum 8 characters, at least one letter and one number)",
            example = "Password123",
            minLength = 8,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    @NotBlank
    @NotNull
    @Schema(
            description = "User first name",
            example = "John",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String firstName;

    @NotBlank
    @NotNull
    @Schema(
            description = "User last name",
            example = "Doe",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String lastName;

    @NotBlank
    @NotNull
    @Email
    @Schema(
            description = "User email address",
            example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;
    @Pattern(regexp = "^\\+20\\s?(10|11|12|15)[0-9]{8}$\n", message = "Invalid phone number format")
    @Schema(
            description = "User phone number",
            example = "+201123456789",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String phoneNumber;

    @Override
    public String toString() {
        return getUsername()+"\n"+getFirstName()+"\n"+getLastName()+"\n"+getEmail();
    }
}
