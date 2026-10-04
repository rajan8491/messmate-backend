package org.example.messmate.repository;

import org.example.messmate.entity.ItemRating;
import org.example.messmate.enums.ItemType;
import org.example.messmate.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ItemRatingRepository
        extends JpaRepository<ItemRating, Long> {

    boolean existsByStudentIdAndItemIdAndItemTypeAndMealAndRatedDate(
            Long studentId,
            Long itemId,
            ItemType itemType,
            MealType meal,
            LocalDate ratedDate
    );

    List<ItemRating> findByItemIdAndItemType(
            Long itemId,
            ItemType itemType
    );
}
