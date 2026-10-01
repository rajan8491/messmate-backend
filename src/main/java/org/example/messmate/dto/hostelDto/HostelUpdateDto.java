package org.example.messmate.dto.hostelDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.enums.Residents;

@Getter
@Setter
public class HostelUpdateDto{
    @NotNull
    private Long id;

    @NotBlank(message = "Hostel name cannot be blank")
    private String name;

    @NotNull(message = "Residents field is required")
    private Residents residents;

    @NotBlank(message = "Hostel email cannot be blank")
    @Email(message = "Invalid email format")
    private String hostelEmail;

    private String accountantName;

    @NotBlank(message = "Login ID cannot be blank")
    private String loginId;

    private String hostelPhone;

    private String accountantPhone;
    
    private String password;
}
