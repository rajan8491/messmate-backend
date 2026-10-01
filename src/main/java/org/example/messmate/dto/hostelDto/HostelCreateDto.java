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
public class HostelCreateDto {
    @NotNull
    private Long id;

    @NotBlank(
            message = "Hostel name cannot be empty"
    )
    @NotNull(
            message = "Hostel name is required"
    )
    private String name;

    @NotNull
    private Residents residents;

    @Email(
            message = "Email should be valid"
    )
    @NotNull
    private String hostelEmail;

    @NotBlank(
            message = "Hostel phone should be 10 digits"
    )
    @Pattern(regexp = "\\d{10}")
    private String hostelPhone;

    //accountant
    @NotBlank(
            message = "Name should not be empty"
    )
    @NotNull
    private String accountantName;

    @NotBlank(
            message = "Accountant phone should be 10 digits"
    )
    @Pattern(regexp = "\\d{10}")
    private String accountantPhone;

    @NotNull
    @NotBlank
    private String loginId;

    @NotNull
    @NotBlank
    private String password;

}
