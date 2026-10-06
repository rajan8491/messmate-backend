package org.example.messmate.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoogleVerifyRequestDto {

    @NotBlank(message = "Google ID token is required")
    private String token;
}
