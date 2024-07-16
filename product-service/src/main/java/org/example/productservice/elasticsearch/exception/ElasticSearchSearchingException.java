package org.example.productservice.elasticsearch.exception;

public class ElasticSearchSearchingException extends RuntimeException {

    public static final String MESSAGE_FORMAT = "Product searching failed: %s";

    public ElasticSearchSearchingException(Exception cause) {
        super(String.format(MESSAGE_FORMAT, cause.getMessage()));
    }
}
