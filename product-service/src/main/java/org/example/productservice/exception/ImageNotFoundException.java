package org.example.productservice.exception;

public class ImageNotFoundException extends RuntimeException {

    public static final String MESSAGE_FORMAT = "Image with url %s not found";

    public ImageNotFoundException(String url) {
        super(String.format(MESSAGE_FORMAT, url));
    }
}
