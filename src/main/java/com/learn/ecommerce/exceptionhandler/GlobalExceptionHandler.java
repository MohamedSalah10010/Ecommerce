package com.learn.ecommerce.exceptionhandler;

import com.learn.ecommerce.DTO.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
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
                .errorMessage(ex.getMessage())
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

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFound ex, WebRequest request)
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

    private void logError(String message, Exception ex) {
        log.error(message, ex);
        ex.printStackTrace();
    }
}
