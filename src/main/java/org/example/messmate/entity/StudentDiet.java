package org.example.messmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.enums.MealType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter

@Entity
@Table(
        name = "student_diet"
)
public class StudentDiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false
    )
    private MealType type;


    private BigDecimal price;
}
