package org.example.messmate.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    public NotificationChannel channel;
    public String recipient;
    public NotificationTemplate template;
    public Map<String, Object> parameters;
}
