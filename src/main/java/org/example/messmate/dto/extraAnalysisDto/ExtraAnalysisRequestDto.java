package org.example.messmate.dto.extraAnalysisDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ExtraAnalysisRequestDto {
    private LocalDate from;
    private LocalDate to;
    private String groupBy;
}
