package org.example.messmate.dto.otpDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.messmate.notification.domain.NotificationChannel;

@Getter
@Setter
@NoArgsConstructor
public class OtpVerifyRequestDto {

    @NotBlank
    private String identifier;

    @NotBlank
    @Pattern(regexp = "\\d{6}")
    private String otp;

    @NotNull
    private NotificationChannel channel;
}
