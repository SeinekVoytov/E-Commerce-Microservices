package org.example.productservice.exception;

import org.example.productservice.elasticsearch.exception.ProductIndexingException;
import org.example.productservice.elasticsearch.exception.ProductUpdatingException;
import org.example.productservice.elasticsearch.exception.SearchTextIsTooShortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            {
                    ProductNotFoundException.class,
                    CategoryNotFoundException.class,
                    ImageNotFoundException.class
            }
    )
    public ResponseEntity<ErrorObject> handleProductNotFoundException(Exception exc) {
        return new ResponseEntity<>(
                buildErrorObject(HttpStatus.NOT_FOUND.value(), exc.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(
            {
                    InvalidQueryParameterException.class,
                    SearchTextIsTooShortException.class
            }
    )
    public ResponseEntity<ErrorObject> handleInvalidQueryParameterException(Exception exc) {
        return new ResponseEntity<>(
                buildErrorObject(HttpStatus.BAD_REQUEST.value(), exc.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(
            {
                    ProductIndexingException.class,
                    ProductUpdatingException.class
            }
    )
    public ResponseEntity<ErrorObject> handleProductIndexingException(ProductIndexingException exc) {
        return new ResponseEntity<>(
                buildErrorObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), exc.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
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
