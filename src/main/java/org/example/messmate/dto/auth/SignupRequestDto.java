package org.example.messmate.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @NotNull
    @Email
    private String username;

    @NotNull
    private Long hostelId;

    @NotNull
    private String rollNo;

    @NotBlank
    @Size(min = 6, max = 18)
    private String password;
}
