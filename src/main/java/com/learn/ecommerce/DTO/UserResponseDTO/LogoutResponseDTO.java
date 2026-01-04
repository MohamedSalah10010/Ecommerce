package com.learn.ecommerce.DTO.UserResponseDTO;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class LogoutResponseDTO {
    private String logoutMessage;
}
