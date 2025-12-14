package com.learn.ecommerce.exception;

public class UserIsNotVerifiedException extends Exception {
    public UserIsNotVerifiedException(String message, boolean emailSent) {
        super(message);
        this.emailSent = emailSent;
    }


    private boolean emailSent;
    public boolean isEmailSent() {
        return emailSent;
    }

}
