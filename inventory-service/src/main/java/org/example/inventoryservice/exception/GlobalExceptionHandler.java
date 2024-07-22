package org.example.inventoryservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

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

    private ErrorObject buildErrorObject(int statusCode, String message) {
        return new ErrorObject(
                statusCode,
                message,
                new Date()
        );
    }
}
