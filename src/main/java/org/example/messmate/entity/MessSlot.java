package org.example.messmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.enums.MealType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter

@Entity
@Table(
        name = "mess_slot",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "day_of_week",
                                "meal_type"
                        }
                )
        }
)
public class MessSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "meal_type",
            nullable = false
    )
    private MealType mealType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "day_of_week",
            nullable = false
    )
    private DayOfWeek dayOfWeek;

    @Column(
            name = "start_time",
            nullable = false
    )
    private LocalTime start;

    @Column(
            name = "end_time",
            nullable = false
    )
    private LocalTime end;

    @OneToMany(
            mappedBy = "messSlot",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<HostelMenuPlan> hostelMenuPlans = new HashSet<>();
}
