package org.example.messmate.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private UserResponseDto user;
    private String accessToken;
}
