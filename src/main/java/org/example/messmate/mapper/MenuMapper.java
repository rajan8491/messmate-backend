package org.example.messmate.mapper;

import org.example.messmate.dto.menudto.DietItemDto;
import org.example.messmate.dto.menudto.ExtraItemDto;
import org.example.messmate.dto.menudto.MenuDto;
import org.example.messmate.dto.menudto.MenuResponseDto;
import org.example.messmate.entity.DietItem;
import org.example.messmate.entity.ExtraItem;
import org.example.messmate.entity.HostelMenuPlan;
import org.example.messmate.entity.MenuDiet;
import org.example.messmate.entity.MenuExtra;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public class MenuMapper {

    private MenuMapper() {
        /* This utility class should not be instantiated */
    }

    public static MenuResponseDto toMenuResponseDto(
            List<HostelMenuPlan> menuPlans,
            Set<MenuDiet> diets,
            Set<MenuExtra> extras
    ) {
        // 1. Group items by menuPlanId
        Map<Long, List<DietItemDto>> dietsByPlanId = diets.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getMenuPlan().getId(),
                        Collectors.mapping(d -> toDietDto(d.getDietItem()), Collectors.toList())
                ));

        Map<Long, List<ExtraItemDto>> extrasByPlanId = extras.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getMenuPlan().getId(),
                        Collectors.mapping(e -> toExtraDto(e.getExtraItem()), Collectors.toList())
                ));

        // 2. Build single day DTO
        MenuResponseDto dto = new MenuResponseDto();
        dto.setWeekDay(menuPlans.getFirst().getMessSlot().getDayOfWeek());
        dto.setMenu(
                toMenuDto(
                        menuPlans,
                        dietsByPlanId,
                        extrasByPlanId
                )
        );
        return dto;
    }

    public static List<MenuResponseDto> toWeeklyMenuResponseDto(
            List<HostelMenuPlan> menuPlans,
            Set<MenuDiet> diets,
            Set<MenuExtra> extras
    ) {
        // 1. Group Diet items by menuPlanId for O(1) slot-level lookup
        Map<Long, List<DietItemDto>> dietsByPlanId = diets.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getMenuPlan().getId(),
                        Collectors.mapping(
                                d -> toDietDto(d.getDietItem()),
                                Collectors.toList()
                        )
                ));

        // 2. Group Extra items by menuPlanId for O(1) slot-level lookup
        Map<Long, List<ExtraItemDto>> extrasByPlanId = extras.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getMenuPlan().getId(),
                        Collectors.mapping(
                                e -> toExtraDto(e.getExtraItem()),
                                Collectors.toList()
                        )
                ));

        // 3. Group plans by DayOfWeek (e.g., MONDAY, TUESDAY)
        Map<DayOfWeek, List<HostelMenuPlan>> plansByDay = menuPlans.stream()
                .collect(Collectors.groupingBy(
                        plan -> plan.getMessSlot().getDayOfWeek(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // 4. Build MenuResponseDto per day
        List<MenuResponseDto> weeklyMenuDtos = new ArrayList<>();

        for (Map.Entry<DayOfWeek, List<HostelMenuPlan>> entry : plansByDay.entrySet()) {
            DayOfWeek day = entry.getKey();
            List<HostelMenuPlan> dayPlans = entry.getValue();

            MenuResponseDto dayDto = new MenuResponseDto();
            dayDto.setWeekDay(day);
            dayDto.setMenu(
                    toMenuDto(
                            dayPlans,
                            dietsByPlanId,
                            extrasByPlanId
                    )
            );

            weeklyMenuDtos.add(dayDto);
        }

        return weeklyMenuDtos;
    }

    private static List<MenuDto> toMenuDto(
            List<HostelMenuPlan> dayPlans,
            Map<Long, List<DietItemDto>> dietsByPlanId,
            Map<Long, List<ExtraItemDto>> extrasByPlanId
    ) {
        List<MenuDto> menusDto = new ArrayList<>();

        for (HostelMenuPlan menuPlan : dayPlans) {
            MenuDto menuDto = new MenuDto();
            menuDto.setType(menuPlan.getMessSlot().getMealType());
            menuDto.setStart(menuPlan.getMessSlot().getStart());
            menuDto.setEnd(menuPlan.getMessSlot().getEnd());

            // Associate only the items that belong to THIS specific menu plan
            menuDto.setDiet(dietsByPlanId.getOrDefault(menuPlan.getId(), Collections.emptyList()));
            menuDto.setExtra(extrasByPlanId.getOrDefault(menuPlan.getId(), Collections.emptyList()));

            menusDto.add(menuDto);
        }

        return menusDto;
    }

    public static DietItemDto toDietDto(DietItem diet) {
        DietItemDto dietDto = new DietItemDto();
        dietDto.setId(diet.getId());
        dietDto.setName(diet.getName());
        return dietDto;
    }

    public static ExtraItemDto toExtraDto(ExtraItem extra) {
        ExtraItemDto extraDto = new ExtraItemDto();
        extraDto.setId(extra.getId());
        extraDto.setName(extra.getName());
        extraDto.setPrice(extra.getPrice());
        return extraDto;
    }
}