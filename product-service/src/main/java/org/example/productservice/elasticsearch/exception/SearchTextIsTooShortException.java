package org.example.productservice.elasticsearch.exception;

public class SearchTextIsTooShortException extends RuntimeException {

    public static final String MESSAGE = "Text for searching is too short, need at least 3 characters";

    public SearchTextIsTooShortException() {
        super(MESSAGE);
    }
}
