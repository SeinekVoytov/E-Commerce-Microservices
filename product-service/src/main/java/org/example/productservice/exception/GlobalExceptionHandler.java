package org.example.productservice.exception;

import org.example.productservice.elasticsearch.exception.ElasticSearchIndexingException;
import org.example.productservice.elasticsearch.exception.ElasticSearchSearchingException;
import org.example.productservice.elasticsearch.exception.ElasticSearchUpdatingException;
import org.example.productservice.elasticsearch.exception.SearchTextIsTooShortException;
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
                    InvalidQueryParameterException.class,
                    CategoryAlreadyExistsException.class,
                    SearchTextIsTooShortException.class,
                    InvalidCategorySelectorException.class
            }
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus400(Exception exc) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exc.getMessage());
    }

    @ExceptionHandler(
            {
                    ProductNotFoundException.class,
                    CategoryNotFoundException.class,
                    ImageNotFoundException.class
            }
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus404(Exception exc) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exc.getMessage());
    }

    @ExceptionHandler(
            {
                    ElasticSearchIndexingException.class,
                    ElasticSearchUpdatingException.class,
                    ElasticSearchSearchingException.class
            }
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus500(ElasticSearchIndexingException exc) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exc.getMessage());
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