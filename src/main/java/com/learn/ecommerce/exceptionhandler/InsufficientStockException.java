package com.learn.ecommerce.exceptionhandler;

public class InsufficientStockException extends RuntimeException {

public InsufficientStockException(String message) {
	super(message);
}
}