package org.example.productservice.elasticsearch.exception;

public class ProductIndexingException extends RuntimeException {

    public ProductIndexingException() {
        super("Product indexing failed");
    }
}
