package org.example.messmate.notification.infrastructure;

import org.example.messmate.notification.domain.NotificationTemplate;

import java.util.Map;

public interface EmailTemplate {
    NotificationTemplate template();

    EmailContent render(Map<String, Object> parameters);
}
