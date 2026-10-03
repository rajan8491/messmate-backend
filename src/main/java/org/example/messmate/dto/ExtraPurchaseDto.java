package org.example.messmate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.enums.MealType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ExtraPurchaseDto {
    private LocalDate date;
    private MealType mealType;
    private List<ExtraPurchaseInfo> items;
}
