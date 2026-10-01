package org.example.messmate.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "otp")
@Getter
@Setter
public class OtpProperties {
    public int length = 6;
    public Duration expiry = Duration.ofMinutes(5);
    public int maxAttempts = 5;
    public Duration resendCooldown = Duration.ofSeconds(60);
}
