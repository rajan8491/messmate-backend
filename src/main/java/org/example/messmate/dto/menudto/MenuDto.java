package org.example.messmate.dto.menudto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.messmate.enums.MealType;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuDto {
    private MealType type;
    private LocalTime start;
    private LocalTime end;
    private List<DietItemDto> diet;
    private List<ExtraItemDto> extra;
}
