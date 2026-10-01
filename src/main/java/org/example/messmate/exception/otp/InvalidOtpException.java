package org.example.messmate.exception.otp;

public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException() {
        super("Otp code is invalid");
    }
    public InvalidOtpException(String message) {
        super(message);
    }
}
