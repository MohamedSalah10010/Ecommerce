package com.learn.ecommerce.DTO.UserRequestDTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class LoginBodyDTO {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;

}
