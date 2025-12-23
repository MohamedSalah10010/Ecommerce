package com.learn.ecommerce.DTO.UserResponseDTO;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserStatusDTO {

    private String email;
    private boolean isEnabled;
    private boolean isVerified;

    private boolean passwordChangedSuccess;
    private String statusMessage;
}
