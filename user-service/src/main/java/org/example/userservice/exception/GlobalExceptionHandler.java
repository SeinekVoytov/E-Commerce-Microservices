package org.example.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            {
                    InvalidCartIdCookieException.class,
                    CartIsEmptyException.class
            }
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus400(Exception exc) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exc.getMessage());
    }

    @ExceptionHandler(
            {
                    ProductNotFoundException.class,
                    CartItemNotFoundException.class,
                    CartNotFoundException.class
            }
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus404(Exception exc) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exc.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorObject> handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
        Map<String, String> errors = new HashMap<>();
        exc.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errors.toString());
    }

    private ResponseEntity<ErrorObject> buildErrorResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(
                new ErrorObject(status.value(), message, new Date()),
                status
        );
    }
}
