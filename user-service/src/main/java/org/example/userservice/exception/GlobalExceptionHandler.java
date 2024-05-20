package org.example.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorObject> handleProductNotFoundException(ProductNotFoundException exc) {
        return new ResponseEntity<>(
                buildErrorObject(HttpStatus.NOT_FOUND.value(), exc.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidCartIdCookieException.class)
    public ResponseEntity<ErrorObject> handleInvalidCartIdException(InvalidCartIdCookieException exc) {
        return new ResponseEntity<>(
                buildErrorObject(HttpStatus.BAD_REQUEST.value(), exc.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    private ErrorObject buildErrorObject(int statusCode, String message) {
        return new ErrorObject(
                statusCode,
                message,
                new Date()
        );
    }
}
