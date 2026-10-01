package org.example.messmate.exception;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException() {
        super("User is not authorized");
    }
}
