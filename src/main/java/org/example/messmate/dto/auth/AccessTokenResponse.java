package org.example.messmate.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
}

