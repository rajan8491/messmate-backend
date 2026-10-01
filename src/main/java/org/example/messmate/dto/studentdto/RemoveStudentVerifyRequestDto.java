package org.example.messmate.dto.studentdto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.dto.otpDto.OtpVerifyRequestDto;

import java.util.List;

@Getter
@Setter
public class RemoveStudentVerifyRequestDto extends OtpVerifyRequestDto {
    @NotNull
    private List<String> studentIdentifiers;
}
