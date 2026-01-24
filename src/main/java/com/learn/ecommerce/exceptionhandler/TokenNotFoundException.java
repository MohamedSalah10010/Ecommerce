package com.learn.ecommerce.exceptionhandler;

public class TokenNotFoundException extends RuntimeException {

	public TokenNotFoundException(String message) {
        super(message);
    }
}