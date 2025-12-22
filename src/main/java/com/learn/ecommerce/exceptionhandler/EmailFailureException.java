package com.learn.ecommerce.exceptionhandler;

public class EmailFailureException extends RuntimeException {
    public EmailFailureException(String message) {
        super(message);
    }
}
