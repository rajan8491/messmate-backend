package org.example.messmate.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.notification.domain.NotificationChannel;

@Getter
@Setter
public class SignupVerifyRequestDto {

    @Email
    @NotNull
    private String identifier;

    @NotBlank
    @Pattern(regexp = "\\d{6}")
    private String otp;

    @NotNull
    private NotificationChannel channel;

}
