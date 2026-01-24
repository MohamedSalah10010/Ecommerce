package com.learn.ecommerce.exceptionhandler;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(String message) {super(message);}
}