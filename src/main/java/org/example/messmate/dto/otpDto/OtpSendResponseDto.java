package org.example.messmate.dto.otpDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpSendResponseDto {
    private String identifier;
    private String message = "OTP Sent Successfully";
}
