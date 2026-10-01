package org.example.messmate.notification.application;

import org.example.messmate.notification.domain.Notification;

public interface NotificationService {
    void send(Notification notification);
}
