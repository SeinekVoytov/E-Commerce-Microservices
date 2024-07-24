package org.example.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            {
                    InvalidQueryParameterException.class,
                    CartIsEmptyException.class
            }
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus400(Exception exc) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exc.getMessage());
    }

    @ExceptionHandler(
            AccessTokenExpiredException.class
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus401(Exception exc) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exc.getMessage());
    }

    @ExceptionHandler(
            {
                    OrderNotFoundException.class,
                    CartNotFoundException.class,
                    ProductNotFoundException.class
            }
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus404(Exception exc) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exc.getMessage());
    }

    private ResponseEntity<ErrorObject> buildErrorResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(
                new ErrorObject(status.value(), message, new Date()),
                status
        );
    }
}
