package org.example.messmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter

@Entity
@Table(
        name = "student_extra"
)
public class StudentExtra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

    @ManyToOne
    @JoinColumn(
            name = "extra_id",
            nullable = false
    )
    private ExtraItem extra;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;
}
