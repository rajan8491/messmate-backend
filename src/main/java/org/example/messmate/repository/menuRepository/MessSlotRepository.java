package org.example.messmate.repository.menuRepository;

import org.example.messmate.entity.MessSlot;
import org.example.messmate.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface MessSlotRepository extends JpaRepository<MessSlot, Long> {

    Optional<MessSlot> findByDayOfWeekAndMealType(
            DayOfWeek day,
            MealType mealType
    );

    @Query("""
        SELECT s FROM MessSlot s
        WHERE s.dayOfWeek = :dayOfWeek
    """)
    List<MessSlot> findSlotsByDay(
            @Param("dayOfWeek") DayOfWeek dayOfWeek
    );

}
