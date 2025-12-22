package com.learn.ecommerce.DTO.UserRequestDTO;

import jakarta.validation.constraints.*;

public class RegistrationBodyDTO {

    @NotBlank
    @NotNull
    @Size(min = 3)
    private String username;

    @NotBlank
    @NotNull
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$") // Minimum eight characters, at least one letter and one number
    private String password;

    @NotBlank
    @NotNull
    private  String firstName;

    @NotBlank
    @NotNull
    private String lastName;

    @NotBlank
    @NotNull
    @Email
    private String email;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return getUsername()+"\n"+getFirstName()+"\n"+getLastName()+"\n"+getEmail();
    }
}
