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
                    OrderNotFoundException.class,
                    CartNotFoundException.class
            }
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus404(OrderNotFoundException exc) {
        return new ResponseEntity<>(
                buildErrorObject(HttpStatus.NOT_FOUND.value(), exc.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(
            {
                    InvalidQueryParameterException.class,
                    CartIsEmptyException.class
            }
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus400(InvalidQueryParameterException exc) {
        return new ResponseEntity<>(
                buildErrorObject(HttpStatus.BAD_REQUEST.value(), exc.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(
            AccessTokenExpiredException.class
    )
    public ResponseEntity<ErrorObject> handleAccessTokenExpiredException(AccessTokenExpiredException exc) {
        return new ResponseEntity<>(
                buildErrorObject(HttpStatus.UNAUTHORIZED.value(), exc.getMessage()),
                HttpStatus.UNAUTHORIZED
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
