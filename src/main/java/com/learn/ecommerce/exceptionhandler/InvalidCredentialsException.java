package com.learn.ecommerce.exceptionhandler;

public class InvalidCredentialsException extends RuntimeException{
	public InvalidCredentialsException(String message) {
        super(message);
	}
}