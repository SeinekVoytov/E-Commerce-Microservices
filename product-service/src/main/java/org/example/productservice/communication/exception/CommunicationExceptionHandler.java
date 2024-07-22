package org.example.productservice.communication.exception;

import org.example.productservice.exception.ErrorObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class CommunicationExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorObject> handleHttpClientErrorException(HttpClientErrorException exc) {
        return new ResponseEntity<>(
                exc.getResponseBodyAs(ErrorObject.class),
                exc.getStatusCode()
        );
    }
}
