package org.example.messmate.notification.application;

import org.example.messmate.notification.domain.Notification;
import org.example.messmate.notification.domain.NotificationChannel;

public interface NotificationSender {
    NotificationChannel channel();
    void send(Notification notification);
}
