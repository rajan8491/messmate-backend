package org.example.messmate.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupVerifyResponseDto {
    String identifier;
    String message;
}
