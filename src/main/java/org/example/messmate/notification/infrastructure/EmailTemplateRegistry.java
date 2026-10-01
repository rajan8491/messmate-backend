package org.example.messmate.notification.infrastructure;

import org.example.messmate.notification.domain.NotificationTemplate;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmailTemplateRegistry {
    private final Map<NotificationTemplate, EmailTemplate> templates;
    public EmailTemplateRegistry(
            List<EmailTemplate> templates
    ) {
        this.templates = new EnumMap<>(NotificationTemplate.class);

        for (EmailTemplate template : templates) {
            this.templates.put(
                    template.template(),
                    template
            );
        }
    }

    public EmailTemplate get(
            NotificationTemplate notificationTemplate
    ) {
        EmailTemplate emailTemplate = templates.get(notificationTemplate);

        if(emailTemplate == null) {
            throw new IllegalArgumentException(
                    String.format(
                            "Notification template '%s' does not exist",
                            notificationTemplate.name()
                    )
            );
        }
        return emailTemplate;
    }
}
