package org.example.productservice.exception;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(String url) {
        super(String.format("Image with url %s not found", url));
    }
}
