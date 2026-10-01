package org.example.messmate.dto.otpDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.messmate.notification.domain.NotificationChannel;

@Getter
@Setter
@NoArgsConstructor
public class OtpSendRequestDto {
    @NotBlank
    private String identifier;

    @NotNull
    private NotificationChannel channel;
}
