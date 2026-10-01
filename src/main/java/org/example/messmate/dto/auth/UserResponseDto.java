package org.example.messmate.dto.auth;

import lombok.*;
import org.example.messmate.enums.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String username;
    private Role role;
    private boolean verified;
}
