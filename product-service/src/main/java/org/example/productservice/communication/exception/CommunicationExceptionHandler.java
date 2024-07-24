package org.example.productservice.communication.exception;

import org.example.productservice.exception.ErrorObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;

@ControllerAdvice
public class CommunicationExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorObject> handleHttpClientErrorException(HttpClientErrorException exc) {
        if (exc.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ResponseEntity<>(
                    new ErrorObject(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Access token has expired",
                            new Date()
                    ),
                    HttpStatus.UNAUTHORIZED
            );
        }

        return new ResponseEntity<>(
                exc.getResponseBodyAs(ErrorObject.class),
                exc.getStatusCode()
        );
    }
}
