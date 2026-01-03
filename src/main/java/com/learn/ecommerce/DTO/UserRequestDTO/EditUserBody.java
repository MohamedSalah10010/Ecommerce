package com.learn.ecommerce.DTO.UserRequestDTO;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditUserBody {

    @Email
    @Schema(
            description = "User email address",
            example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;
    @Pattern(regexp = "^\\+20\\s?(10|11|12|15)[0-9]{8}$", message = "Invalid phone number format")
    @Schema(
            description = "User phone number",
            example = "+201123456789",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String phoneNumber;
    private String username;
    private String firstName;
    private String lastName;

}
