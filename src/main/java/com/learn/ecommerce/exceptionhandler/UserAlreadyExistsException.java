package com.learn.ecommerce.exceptionhandler;


public class UserAlreadyExistsException extends RuntimeException {
public UserAlreadyExistsException(String message) {super(message);}
}