package com.learn.ecommerce.exceptionhandler;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserIsNotVerifiedException extends RuntimeException {



    private boolean emailSent;

    public boolean isEmailSent()
    {
        return emailSent;
    }

}
