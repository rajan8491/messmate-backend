package org.example.messmate.notification.application;

import org.example.messmate.notification.domain.Notification;
import org.example.messmate.notification.domain.NotificationChannel;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class DefaultNotificationService implements NotificationService{
    private final Map<NotificationChannel,NotificationSender> senders;
    public DefaultNotificationService(
            List<NotificationSender> senders
    ){
        this.senders = new EnumMap<>(NotificationChannel.class);

        for(NotificationSender sender : senders){
            this.senders.put(sender.channel(), sender);
        }
    }
    @Override
    public void send(Notification notification) {
        NotificationSender sender = senders.get(notification.channel);

        if(sender == null){
            throw new IllegalStateException(
                    "No notification sender registered for notification channel " + notification.channel
            );
        }
        sender.send(notification);
    }
}
