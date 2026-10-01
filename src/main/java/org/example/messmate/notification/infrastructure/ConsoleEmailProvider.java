package org.example.messmate.notification.infrastructure;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class ConsoleEmailProvider implements EmailProvider {

    @Override
    public void send(
            String recipient,
            String subject,
            String message
    ) {
        System.out.println("DEV OTP : " + message);
    }
}
