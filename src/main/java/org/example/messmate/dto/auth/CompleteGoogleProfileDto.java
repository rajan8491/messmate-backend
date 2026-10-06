package org.example.messmate.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompleteGoogleProfileDto {

    @NotBlank(message = "Google token is required")
    private String token;

    @NotBlank(message = "Full name is required")
    private String name;

    @NotNull(message = "Hostel ID is required")
    private Long hostelId;

    @NotBlank(message = "Roll number is required")
    private String rollNo;
}
