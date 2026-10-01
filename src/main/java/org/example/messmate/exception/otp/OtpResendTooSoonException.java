package org.example.messmate.exception.otp;

public class OtpResendTooSoonException extends RuntimeException {
    public OtpResendTooSoonException() {
        super("Otp resend to soon");
    }
    public OtpResendTooSoonException(String message) {
        super(message);
    }
}
