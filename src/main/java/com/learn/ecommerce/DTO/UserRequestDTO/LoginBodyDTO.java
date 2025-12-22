package com.learn.ecommerce.DTO.UserRequestDTO;

import jakarta.validation.constraints.NotEmpty;

public class LoginBodyDTO {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    public String getUsername() {
        return username;
    }
   public String getPassword() {
        return password;
    }
//    public void setUsername(String username) {
//        this.username = username;
//    }
//    public void setPassword(String password) {
//        this.password = password;
//    }
}
