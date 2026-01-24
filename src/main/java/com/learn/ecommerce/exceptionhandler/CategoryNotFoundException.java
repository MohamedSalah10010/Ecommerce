package com.learn.ecommerce.exceptionhandler;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(String message) {
	    super(message);
    }
}