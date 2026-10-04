package org.example.messmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.enums.ItemType;
import org.example.messmate.enums.MealType;

import java.time.LocalDate;

@Entity
@Table(
        name = "item_rating",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_item_meal_date",
                        columnNames = {
                                "student_id",
                                "item_id",
                                "item_type",
                                "meal",
                                "rated_date"
                        }
                )
        }
)
@Getter
@Setter
public class ItemRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal", nullable = false)
    private MealType meal;

    @Column(nullable = false)
    private Integer rating;

    @Column(
            name = "tags",
            columnDefinition = "text[]"
    )
    private String[] tags;

    @Column(length = 100)
    private String suggestion;

    @Column(
            name = "rated_date",
            nullable = false
    )
    private LocalDate ratedDate;
}