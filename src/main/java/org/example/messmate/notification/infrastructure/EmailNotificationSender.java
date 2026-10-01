package org.example.messmate.notification.infrastructure;

import org.example.messmate.notification.application.NotificationSender;
import org.example.messmate.notification.domain.Notification;
import org.example.messmate.notification.domain.NotificationChannel;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {

    private final EmailProvider emailProvider;
    private final EmailTemplateRegistry emailTemplateRegistry;

    public EmailNotificationSender(
            EmailProvider emailProvider,
            EmailTemplateRegistry emailTemplateRegistry
    ) {
        this.emailProvider = emailProvider;
        this.emailTemplateRegistry = emailTemplateRegistry;
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(Notification notification) {
        EmailTemplate template =
                emailTemplateRegistry
                        .get(notification.getTemplate());

        EmailContent emailContent =
                template
                        .render(notification.getParameters());

        emailProvider
                .send(
                        notification.getRecipient(),
                        emailContent.getSubject(),
                        emailContent.getBody()
                );
    }
}
