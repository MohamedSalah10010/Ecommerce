package com.learn.ecommerce.DTO.UserResponseDTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "User status response")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserStatusDTO {

    @Schema(
            description = "User email address",
            example = "john.doe@example.com"
    )
    private String email;

    @Schema(
            description = "Indicates whether the user account is enabled",
            example = "true"
    )
    private boolean isEnabled;

    @Schema(
            description = "Indicates whether the user email is verified",
            example = "true"
    )
    private boolean isVerified;

    @Schema(
            description = "Indicates whether password change was successful",
            example = "true"
    )
    private boolean passwordChangedSuccess;

    @Schema(
            description = "Status or informational message",
            example = "Password changed successfully"
    )
    private String statusMessage;
}