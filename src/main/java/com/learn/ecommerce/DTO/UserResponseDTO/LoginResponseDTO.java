package com.learn.ecommerce.DTO.UserResponseDTO;


import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class LoginResponseDTO {

    private String jwt;

    private boolean success;
    private String failureMessage;



}

