package org.example.productservice.elasticsearch.exception;

public class ElasticSearchIndexingException extends RuntimeException {

    public static final String MESSAGE_FORMAT = "Product indexing failed: %s";

    public ElasticSearchIndexingException(Exception cause) {
        super(String.format(MESSAGE_FORMAT, cause.getMessage()));
    }
}
