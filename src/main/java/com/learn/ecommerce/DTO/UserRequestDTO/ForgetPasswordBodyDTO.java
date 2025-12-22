package com.learn.ecommerce.DTO.UserRequestDTO;

import jakarta.validation.constraints.NotEmpty;

public class ForgetPasswordBodyDTO {

    @NotEmpty
    private String email;
    @NotEmpty
    private  String username;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
