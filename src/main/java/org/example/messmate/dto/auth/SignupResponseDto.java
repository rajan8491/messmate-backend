package org.example.messmate.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupResponseDto {
    @NotNull
    @NotBlank
    private String identifier;

    @NotNull
    @NotBlank
    private String message;
}
