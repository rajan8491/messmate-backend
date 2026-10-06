package org.example.messmate.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleVerifyResponseDto {

    private boolean isNewUser;
    private String email;
    private String name;

    // Populated if user already exists
    private String accessToken;
    private String role;
    private String username;
}