package com.learn.ecommerce.exceptionhandler;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String message) {
        super(message);
    }
}
