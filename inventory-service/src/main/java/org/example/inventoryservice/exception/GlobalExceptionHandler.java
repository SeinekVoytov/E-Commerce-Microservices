package org.example.inventoryservice.exception;

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
                    InventoryItemAlreadyExistsException.class,
                    NotSufficientAmountOfProductException.class
            }
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus400(Exception exc) {
        return new ResponseEntity<>(
                buildErrorObject(HttpStatus.BAD_REQUEST.value(), exc.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(
            InventoryItemNotFoundException.class
    )
    public ResponseEntity<ErrorObject> handleExceptionWithResponseStatus404(Exception exc) {
        return new ResponseEntity<>(
                buildErrorObject(HttpStatus.NOT_FOUND.value(), exc.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorObject> handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
        Map<String, String> errors = new HashMap<>();
        exc.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(
                buildErrorObject(HttpStatus.BAD_REQUEST.value(), errors.toString()),
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
