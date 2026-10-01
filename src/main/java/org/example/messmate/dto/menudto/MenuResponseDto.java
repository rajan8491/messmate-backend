package org.example.messmate.dto.menudto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuResponseDto {
    private DayOfWeek weekDay;
    private List<MenuDto> menu;
}
