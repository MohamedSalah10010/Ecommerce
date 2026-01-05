package com.learn.ecommerce.DTO.UserResponseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.learn.ecommerce.entity.Address;
import com.learn.ecommerce.entity.UserRoles;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.util.Collection;

@Schema(description = "User profile information")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDTO {

    @Schema(example = "1")
    private Long id;

    @JsonProperty("userName")
    @Schema(example = "john_doe")
    private String userName;

    @JsonProperty("email")
    @Schema(example = "john.doe@example.com")
    private String email;

    @JsonProperty("first_name")
    @Schema(example = "John")
    private String firstName;

    @JsonProperty("last_name")
    @Schema(example = "Doe")
    private String lastName;

    private String phoneNumber;
    private Collection<UserRoles> roles;

    @Schema(
            description = "List of user addresses",
            nullable = true
    )
    private Collection<Address> addresses;

    @JsonProperty("is_enabled")
    @Schema(example = "true")
    private Boolean isEnabled;

    @JsonProperty("is_verified")
    @Schema(example = "true")
    private Boolean isVerified;

    @JsonProperty("created_at")
    @Schema(example = "2023-10-01T12:34:56Z")
    private Instant createdAt;

    @JsonProperty("updated_at")
    @Schema(example = "2023-10-02T12:34:56Z")
    private Instant updatedAt;

    @JsonProperty("created_by")
    @Schema(example = "admin_user")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(example = "admin_user")
    private String updatedBy;

}