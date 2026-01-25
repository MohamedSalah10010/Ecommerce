package com.learn.ecommerce.DTO.UserResponseDTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Schema(description = "User status response")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private Boolean isEnabled;

    @Schema(
            description = "Indicates whether the user email is verified",
            example = "true"
    )
    private Boolean isVerified;

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


    @JsonProperty("updated_at")
    @Schema(example = "2023-10-02T12:34:56Z")
    private Instant updatedAt;

    @JsonProperty("updated_by")
    @Schema(example = "user")
    private String updatedBy;
}