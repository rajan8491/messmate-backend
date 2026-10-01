package org.example.messmate.dto.menudto;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
public class UpdateTodayMenuDto {
    private DayOfWeek weekDay;
    private List<MenuDto> menu;
}
