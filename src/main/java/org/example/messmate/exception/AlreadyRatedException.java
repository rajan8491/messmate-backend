package org.example.messmate.exception;

public class AlreadyRatedException extends RuntimeException {
    public AlreadyRatedException(String message) {
        super(message);
    }
}
