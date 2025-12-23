package com.learn.ecommerce.DTO.UserResponseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.learn.ecommerce.entity.Address;
import lombok.*;

import java.util.Collection;

@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDTO {
    private Long id;

    @JsonProperty( "userName")
    private String userName;


    @JsonProperty( "email")
    private String email;

    @JsonProperty( "first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private Collection<Address> addresses ;

    @JsonProperty("is_enabled")
    private Boolean isEnabled ;

    @JsonProperty("is_verified")
    private Boolean isVerified ;

}
