package org.example.productservice.elasticsearch.exception;

public class ElasticSearchUpdatingException extends RuntimeException {

    public static final String MESSAGE_FORMAT = "Product updating failed: %s";

    public ElasticSearchUpdatingException(Exception cause) {
        super(String.format(MESSAGE_FORMAT, cause.getMessage()));
    }
}
