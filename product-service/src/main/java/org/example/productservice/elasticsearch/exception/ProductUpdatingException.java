package org.example.productservice.elasticsearch.exception;

public class ProductUpdatingException extends RuntimeException {

    public ProductUpdatingException() {
        super("Product searching failed");
    }
}
