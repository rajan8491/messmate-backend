package org.example.messmate.service;

import org.example.messmate.dto.menudto.*;
import org.example.messmate.entity.*;
import org.example.messmate.enums.MealType;
import org.example.messmate.exception.ResourceNotFoundException;
import org.example.messmate.mapper.MenuMapper;
import org.example.messmate.repository.menuRepository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MenuService {
    private final MenuDietRepository menuDietRepository;
    private final MenuExtraRepository menuExtraRepository;
    private final HostelMenuPlanRepository hostelMenuPlanRepository;
    private final MessSlotRepository messSlotRepository;
    private final DietItemRepository dietItemRepository;
    private final ExtraItemRepository extraItemRepository;

    public MenuService(
            MenuDietRepository menuDietRepository,
            MenuExtraRepository menuExtraRepository,
            HostelMenuPlanRepository hostelMenuPlanRepository,
            MessSlotRepository messSlotRepository,
            DietItemRepository dietItemRepository,
            ExtraItemRepository extraItemRepository
    ) {
        this.menuDietRepository = menuDietRepository;
        this.menuExtraRepository = menuExtraRepository;
        this.hostelMenuPlanRepository = hostelMenuPlanRepository;
        this.messSlotRepository = messSlotRepository;
        this.dietItemRepository = dietItemRepository;
        this.extraItemRepository = extraItemRepository;
    }

    public MenuResponseDto getMenuByHostelAndDay(
            Long hostelId,
            DayOfWeek day
    ) {
        // fetch mess_slot by day
        List<HostelMenuPlan> menuPlans =
                hostelMenuPlanRepository.findSlotsByHostelAndDay(
                        hostelId,
                        day
                );

        List<Long> menuPlanIds =
                menuPlans.stream()
                        .map(HostelMenuPlan::getId)
                        .toList();

        // now fetch dietItems using join -> prevent n + 1 query
        Set<MenuDiet> diets =
                menuDietRepository.findActiveDietsByMenuPlanIds(menuPlanIds);

        // now fetch extraItems using join
        Set<MenuExtra> extras =
                menuExtraRepository.findActiveExtrasByMenuPlanIds(menuPlanIds);

        // now map the mess_slot to menu_response
        return MenuMapper.toMenuResponseDto(
                day,
                menuPlans,
                diets,
                extras
        );
    }

    public MenuResponseDto getTodayMenu(Long hostelId) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return getMenuByHostelAndDay(
                hostelId,
                today
        );
    }


    public MenuResponseDto getDayMenu(
            Long hostelId,
            String day
    ) {
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
        return getMenuByHostelAndDay(
                hostelId,
                dayOfWeek
        );
    }

    public List<MenuResponseDto> getWeeklyMenu(
            Long hostelId
    ) {
        List<HostelMenuPlan> menuPlans =
                hostelMenuPlanRepository.findSlotsByHostel(hostelId);

        List<Long> menuPlanIds =
                menuPlans.stream()
                        .map(HostelMenuPlan::getId)
                        .toList();

        // now fetch dietItems using join -> prevent n + 1 query
        Set<MenuDiet> diets =
                menuDietRepository.findActiveDietsByMenuPlanIds(menuPlanIds);

        // now fetch extraItems using join
        Set<MenuExtra> extras =
                menuExtraRepository.findActiveExtrasByMenuPlanIds(menuPlanIds);

        return MenuMapper.toWeeklyMenuResponseDto(
                menuPlans,
                diets,
                extras
        );
    }

    @Transactional
    public void updateWeeklyMenu(
            List<WeeklyMenuUpdateDto> weeklyMenuUpdateDto,
            Long hostelId
    ) {
        // 1. Gather all incoming Diet and Extra IDs across all days to batch fetch active catalog items
        Set<Long> incomingDietIds = new HashSet<>();
        Set<Long> incomingExtraIds = new HashSet<>();

        for (WeeklyMenuUpdateDto dayDto : weeklyMenuUpdateDto) {
            if (dayDto.getMenu() == null) continue;
            extractItemId(incomingDietIds, incomingExtraIds, dayDto.getMenu());
        }

        // 2. Fetch master catalog items in batch
        Map<Long, DietItem> activeDietCatalog =
                dietItemRepository
                        .findAllByIdIn(incomingDietIds)
                        .stream()
                        .collect(Collectors.toMap(
                                DietItem::getId,
                                Function.identity()
                        ));

        Map<Long, ExtraItem> activeExtraCatalog =
                extraItemRepository
                        .findAllByIdIn(incomingExtraIds)
                        .stream()
                        .collect(Collectors.toMap(
                                ExtraItem::getId,
                                Function.identity()
                        ));

        // 3. Fetch existing HostelMenuPlans for this hostel to update them in-place
        // Key format: "MONDAY_BREAKFAST"
        List<HostelMenuPlan> existingPlans =
                hostelMenuPlanRepository
                        .findSlotsByHostel(hostelId);

        Map<String, HostelMenuPlan> planLookup =
                existingPlans.stream()
                        .collect(Collectors.toMap(
                                p ->
                                        p.getMessSlot().getDayOfWeek().name() +
                                                "_" +
                                                p.getMessSlot().getMealType().name(),
                                Function.identity()
                        ));

        List<HostelMenuPlan> plansToPersist = new ArrayList<>();

        // 4. Reconcile each day and meal slot
        for (WeeklyMenuUpdateDto dayDto : weeklyMenuUpdateDto) {
            DayOfWeek day = dayDto.getWeekDay();
            if (dayDto.getMenu() == null) continue;

            for (MenuDto menuDto : dayDto.getMenu()) {
                MealType mealType = menuDto.getType();

                String lookupKey =
                        day.name() + "_" + mealType.name();

                HostelMenuPlan plan = planLookup.get(lookupKey);

                if (plan == null) {
                    MessSlot slot =
                            messSlotRepository
                                    .findByDayOfWeekAndMealType(
                                            day,
                                            mealType
                                    )
                                    .orElseThrow(() ->
                                            new ResourceNotFoundException(
                                                    "MessSlot not found for " +
                                                            day +
                                                            " " +
                                                            menuDto.getType()
                                            )
                                    );

                    plan = new HostelMenuPlan();
                    plan.setHostelId(hostelId);
                    plan.setMessSlot(slot);
                }

                // Update slot timings if provided
                if (menuDto.getStart() != null) plan.getMessSlot().setStart(menuDto.getStart());
                if (menuDto.getEnd() != null) plan.getMessSlot().setEnd(menuDto.getEnd());

                // Reconcile Diet Items
                reconcileDietItems(
                        plan,
                        menuDto.getDiet(),
                        activeDietCatalog
                );

                // Reconcile Extra Items
                reconcileExtraItems(
                        plan,
                        menuDto.getExtra(),
                        activeExtraCatalog
                );

                plansToPersist.add(plan);
            }
        }
        existingPlans.addAll(plansToPersist);
        hostelMenuPlanRepository.saveAll(plansToPersist);

    }

    private void extractItemId(Set<Long> incomingDietIds, Set<Long> incomingExtraIds, List<MenuDto> menu2) {
        for (MenuDto menu : menu2) {
            if (menu.getDiet() != null) {
                menu.getDiet().stream()
                        .map(DietItemDto::getId)
                        .filter(Objects::nonNull)
                        .forEach(incomingDietIds::add);
            }
            if (menu.getExtra() != null) {
                menu.getExtra().stream()
                        .map(ExtraItemDto::getId)
                        .filter(Objects::nonNull)
                        .forEach(incomingExtraIds::add);
            }
        }
    }

    private void reconcileDietItems(
            HostelMenuPlan plan,
            List<DietItemDto> incomingDiets,
            Map<Long, DietItem> activeDietCatalog
    ) {
        // Collect targeted IDs for this slot (empty set if list is null or empty)
        Set<Long> targetIds = (incomingDiets == null) ? Collections.emptySet() :
                incomingDiets.stream()
                        .map(DietItemDto::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        // Map existing links by dietItem ID
        Map<Long, MenuDiet> existingLinksByItemId =
                plan.getDiets()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        md -> md.getDietItem().getId(),
                                        Function.identity(),
                                        (a, b) -> a
                                )
                        );

        // Step A: If an existing link is NOT in targetIds (or targetIds is empty), set active = false
        // invalidate previous menu
        for (MenuDiet existingLink : plan.getDiets()) {
            Long itemId = existingLink.getDietItem().getId();
            if (!targetIds.contains(itemId)) {
                existingLink.setActive(false);
            }
        }

        // Step B: If in targetIds, set active = true (reactivate or insert new)
        for (Long targetId : targetIds) {
            if (existingLinksByItemId.containsKey(targetId)) {
                existingLinksByItemId.get(targetId).setActive(true);
            } else {
                DietItem catalogItem = activeDietCatalog.get(targetId);
                if (catalogItem != null) {
                    MenuDiet newLink = new MenuDiet();
                    newLink.setMenuPlan(plan);
                    newLink.setDietItem(catalogItem);
                    newLink.setActive(true);
                    plan.getDiets().add(newLink);
                }
            }
        }
    }

    private void reconcileExtraItems(
            HostelMenuPlan plan,
            List<ExtraItemDto> incomingExtras,
            Map<Long, ExtraItem> activeExtraCatalog
    ) {
        Set<Long> targetIds = (incomingExtras == null) ? Collections.emptySet() :
                incomingExtras.stream()
                        .map(ExtraItemDto::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        Map<Long, MenuExtra> existingLinksByItemId =
                plan.getExtras()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        me -> me.getExtraItem().getId(),
                                        Function.identity(),
                                        (a, b) -> a
                                )
                        );

        for (MenuExtra existingLink : plan.getExtras()) {
            Long itemId = existingLink.getExtraItem().getId();
            if (!targetIds.contains(itemId)) {
                existingLink.setActive(false);
            }
        }

        for (Long targetId : targetIds) {
            if (existingLinksByItemId.containsKey(targetId)) {
                existingLinksByItemId.get(targetId).setActive(true);
            } else {
                ExtraItem catalogItem = activeExtraCatalog.get(targetId);
                if (catalogItem != null) {
                    MenuExtra newLink = new MenuExtra();
                    newLink.setMenuPlan(plan);
                    newLink.setExtraItem(catalogItem);
                    newLink.setActive(true);
                    plan.getExtras().add(newLink);
                }
            }
        }
    }


    public ItemCatalog getItemsCatalog() {
        List<DietItem> dietItems =
                dietItemRepository.findAll();

        List<ExtraItem> extraItems =
                extraItemRepository.findAll();

        List<DietItemDto> dietItemsDto =
                dietItems.stream()
                        .map(MenuMapper::toDietDto)
                        .toList();

        List<ExtraItemDto> extraItemsDto =
                extraItems.stream()
                        .map(MenuMapper::toExtraDto)
                        .toList();
        
        return new ItemCatalog(dietItemsDto, extraItemsDto);
    }

    @Transactional
    public void updateTodayMenu(
            Long hostelId,
            UpdateTodayMenuDto updateTodayMenuDto
    ) {

        DayOfWeek today = LocalDate.now().getDayOfWeek();

        // 1. Collect all Diet and Extra IDs across all slots for this day
        Set<Long> incomingDietIds = new HashSet<>();
        Set<Long> incomingExtraIds = new HashSet<>();

        extractItemId(incomingDietIds, incomingExtraIds, updateTodayMenuDto.getMenu());

        // 2. Batch fetch active catalog items (prevents N+1 queries)
        Map<Long, DietItem> activeDietCatalog =
                dietItemRepository.findAllByIdIn(incomingDietIds)
                        .stream().
                        collect(Collectors.toMap(DietItem::getId, Function.identity()));

        Map<Long, ExtraItem> activeExtraCatalog =
                extraItemRepository.findAllByIdIn(incomingExtraIds)
                        .stream()
                        .collect(Collectors.toMap(ExtraItem::getId, Function.identity()));

        // 3. Load existing menu plans for this weekday
        List<HostelMenuPlan> existingPlans =
                hostelMenuPlanRepository.findSlotsByHostelAndDay(hostelId, today);

        Map<MealType, HostelMenuPlan> planByMealType =
                existingPlans.stream()
                        .collect(
                                Collectors.toMap(
                                        p -> p.getMessSlot().getMealType(),
                                        Function.identity())
                        );

        List<HostelMenuPlan> plansToPersist = new ArrayList<>();

        // 4. Process each incoming meal slot
        for (MenuDto slotDto : updateTodayMenuDto.getMenu()) {
            MealType mealType = slotDto.getType();
            HostelMenuPlan plan = planByMealType.get(mealType);

            // Resolve or create MessSlot and HostelMenuPlan if not present
            if (plan == null) {
                MessSlot slot =
                        messSlotRepository.findByDayOfWeekAndMealType(
                                today,
                                mealType
                        ).orElseGet(
                                () -> {
                                    MessSlot newSlot = new MessSlot();
                                    newSlot.setDayOfWeek(today);
                                    newSlot.setMealType(mealType);
                                    return messSlotRepository.save(newSlot);
                                });

                plan = new HostelMenuPlan();
                plan.setHostelId(hostelId);
                plan.setMessSlot(slot);
            }

            // Update slot timings if sent by client
            MessSlot messSlot = plan.getMessSlot();
            if (slotDto.getStart() != null) messSlot.setStart(slotDto.getStart());
            if (slotDto.getEnd() != null) messSlot.setEnd(slotDto.getEnd());

            // Reconcile Diet Items (sets isActive = false for omitted items)
            reconcileDietItems(
                    plan,
                    slotDto.getDiet(),
                    activeDietCatalog
            );

            // Reconcile Extra Items (sets isActive = false for omitted items)
            reconcileExtraItems(
                    plan,
                    slotDto.getExtra(),
                    activeExtraCatalog
            );

            plansToPersist.add(plan);
        }

        // 5. Persist all updated plans and junction relations
        hostelMenuPlanRepository.saveAll(plansToPersist);
    }

    public List<ExtraItemDto> getExtraItemByDate(
            LocalDate date,
            String meal,
            Long hostelId
    ) {
        MealType mealType = MealType.valueOf(meal.toUpperCase());
        // 1. find dayOfWeek using date
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        // 2. find slot and menu plan
        Optional<MessSlot> messSlot =
                messSlotRepository.findByDayOfWeekAndMealType(dayOfWeek, mealType);

        if(messSlot.isEmpty()){
            throw new ResourceNotFoundException("MessSlot not found");
        }

        Optional<HostelMenuPlan> menuPlan =
                hostelMenuPlanRepository.findByHostelIdAndMessSlotId(hostelId, messSlot.get().getId());

        if(menuPlan.isEmpty()){
            throw new ResourceNotFoundException("HostelMenuPlan not found");
        }


        // 3. find menu_diet whose creation date is just smaller than date
        List<MenuExtra> menuExtras =
                menuExtraRepository.findExtras(menuPlan.get().getId());

        return menuExtras.stream()
                        .map(MenuExtra::getExtraItem)
                        .map(MenuMapper::toExtraDto)
                        .toList();
    }
}
