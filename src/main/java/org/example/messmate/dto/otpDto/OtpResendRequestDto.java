package org.example.messmate.dto.otpDto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.enums.OtpPurpose;
import org.example.messmate.notification.domain.NotificationChannel;

@Getter
@Setter
public class OtpResendRequestDto {
    @NotNull
    private String identifier;

    @NotNull
    private NotificationChannel channel;

    @NotNull
    private OtpPurpose purpose;
}
