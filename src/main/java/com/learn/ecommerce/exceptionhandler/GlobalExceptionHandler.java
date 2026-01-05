package com.learn.ecommerce.exceptionhandler;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.learn.ecommerce.DTO.ErrorResponseDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@Getter

@Builder

@ControllerAdvice

public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception exception, WebRequest request)
    {
        log.error(exception.getMessage(), exception);

        return new ResponseEntity<>(ErrorResponseDTO
                .builder()
                .errorStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorDescription(request.getDescription(true))
                .errorMessage(exception.getMessage())
                .errorTimestamp(LocalDateTime.now())
        .build(),HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @ExceptionHandler(EmailFailureException.class)
    public ResponseEntity<?> handleEmailFailureException(EmailFailureException ex, WebRequest request)
    {
        logError("Email failure occurred", ex);
        return new ResponseEntity<>(ErrorResponseDTO
                .builder()
                .errorStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorDescription(request.getDescription(true))
                .errorMessage(ex.getMessage())
                .errorTimestamp(LocalDateTime.now())
                .build(),HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request)
    {
        logError("User already exists", ex);
        return new ResponseEntity<>(ErrorResponseDTO
                .builder()
                .errorStatus(HttpStatus.CONFLICT)
                .errorDescription(request.getDescription(true))
                .errorMessage("User with given email already exists")
                .errorTimestamp(LocalDateTime.now())
                .build(),HttpStatus.CONFLICT);

    }

    @ExceptionHandler(UserIsNotVerifiedException.class)
    public ResponseEntity<?> handleUserIsNotVerifiedException(UserIsNotVerifiedException ex, WebRequest request) {
        logError("User is not verified", ex);
        return new ResponseEntity<>(ErrorResponseDTO
                .builder()
                .errorStatus(HttpStatus.FORBIDDEN)
                .errorDescription(request.getDescription(true))
                .errorMessage("User email is not verified.")
                .errorTimestamp(LocalDateTime.now())
                .build(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex, WebRequest request)
    {
        logError("User not found", ex);
        return new ResponseEntity<>(ErrorResponseDTO
                .builder()
                .errorStatus(HttpStatus.NOT_FOUND)
                .errorDescription(request.getDescription(true))
                .errorMessage("User not found")
                .errorTimestamp(LocalDateTime.now())
                .build(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<?> handleTokenNotFoundException(TokenNotFoundException ex, WebRequest request)
    {
        logError("Token not found", ex);
        return new ResponseEntity<>(ErrorResponseDTO
                .builder()
                .errorStatus(HttpStatus.NOT_FOUND)
                .errorDescription(request.getDescription(true))
                .errorMessage("Token not found ")
                .errorTimestamp(LocalDateTime.now())
                .build(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public  ResponseEntity<?> handleTokenExpiredException(TokenExpiredException ex, WebRequest request)
    {
        logError("Token expired", ex);
        return new ResponseEntity<>(ErrorResponseDTO
                .builder()
                .errorStatus(HttpStatus.UNAUTHORIZED)
                .errorDescription(request.getDescription(true))
                .errorMessage("Token has expired")
                .errorTimestamp(LocalDateTime.now())
                .build(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request)
    {
        logError("Invalid credentials", ex);
        return new ResponseEntity<>(ErrorResponseDTO
                .builder()
                .errorStatus(HttpStatus.UNAUTHORIZED)
                .errorDescription(request.getDescription(true))
                .errorMessage("Invalid username or password")
                .errorTimestamp(LocalDateTime.now())
                .build(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<?> handlePasswordMismatchException(PasswordMismatchException ex, WebRequest request)
    {
        logError("Password mismatch", ex);
        return new ResponseEntity<>(ErrorResponseDTO
                .builder()
                .errorStatus(HttpStatus.BAD_REQUEST)
                .errorDescription(request.getDescription(true))
                .errorMessage("Password and confirm password do not match")
                .errorTimestamp(LocalDateTime.now())
                .build(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleProductNotFoundException(ProductNotFoundException ex, WebRequest request)
    {
        logError("Product not found", ex);
        return new ResponseEntity<>(ErrorResponseDTO
                .builder()
                .errorStatus(HttpStatus.NOT_FOUND)
                .errorDescription(request.getDescription(true))
                .errorMessage("Product not found")
                .errorTimestamp(LocalDateTime.now())
                .build(),HttpStatus.NOT_FOUND);
    }

    private void logError(String message, Exception ex) {
        log.error(message, ex);
        ex.printStackTrace();
    }
}
