package org.example.messmate.dto.studentdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDto {
    private String name;
    private String email;
    private String rollNumber;
    private Long hostelId;
    private String hostelName;
}
