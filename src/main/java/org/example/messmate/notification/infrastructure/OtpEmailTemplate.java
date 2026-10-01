package org.example.messmate.notification.infrastructure;

import org.example.messmate.notification.domain.NotificationTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OtpEmailTemplate implements EmailTemplate{
    @Override
    public NotificationTemplate template() {
        return NotificationTemplate.OTP_VERIFICATION;
    }

    @Override
    public EmailContent render(Map<String, Object> parameters) {
        String otp = (String) parameters.get("otp");

        return new EmailContent(
                "Your verification code",
                """
                Your verification code is:

                %s

                This code will expire shortly.

                If you did not request this code, you can safely ignore this email.
                """.formatted(otp)
        );
    }
}
