package org.example.messmate.dto.hostelDto;

import lombok.Getter;
import lombok.Setter;
import org.example.messmate.enums.Residents;

@Getter
@Setter
public class HostelResponseDto {
    private Long id;
    private String name;
    private Residents residents;
    private String hostelEmail;
    private String hostelPhone;
    private String accountantName;
    private String accountantPhone;
    private String loginId;
}
