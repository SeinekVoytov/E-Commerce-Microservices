package org.example.productservice.exception;

import java.util.Collection;

public class ProductNotFoundException extends RuntimeException {

    private static final String MESSAGE_FORMAT_SINGLE_ID = "Product with id = %s could not be found";
    private static final String MESSAGE_FORMAT_MULTIPLE_ID = "Product with ids = %s could not be found";

    public ProductNotFoundException(Integer id) {
        super(String.format(MESSAGE_FORMAT_SINGLE_ID, id));
    }

    public ProductNotFoundException(Collection<Integer> ids) {
        super(String.format(MESSAGE_FORMAT_MULTIPLE_ID, ids));
    }
}
