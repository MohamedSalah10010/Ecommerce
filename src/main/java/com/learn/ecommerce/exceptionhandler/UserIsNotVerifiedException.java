package com.learn.ecommerce.exceptionhandler;

public class UserIsNotVerifiedException extends RuntimeException {
    public UserIsNotVerifiedException( boolean emailSent) {

        this.emailSent = emailSent;
    }


    private boolean emailSent;
    public boolean isEmailSent() {
        return emailSent;
    }

}
