package org.example.messmate.notification.infrastructure;

public interface EmailProvider {
    void send(
            String recipient,
            String subject,
            String message
    );
}
