package org.example.messmate.notification.infrastructure;

import org.example.messmate.notification.domain.NotificationTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WelcomeEmailTemplate implements EmailTemplate{

    @Override
    public NotificationTemplate template() {
        return NotificationTemplate.WELCOME;
    }

    @Override
    public EmailContent render(Map<String, Object> parameters) {
        String name = (String) parameters.get("name");

        return new EmailContent(
                "Welcome to our application",
                """
                Hello %s,

                Welcome to our application.
                """.formatted(name)
        );
    }
}
