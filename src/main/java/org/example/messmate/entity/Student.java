package org.example.messmate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.enums.Role;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Column(nullable = false)
    private String name;

    //@NotNull
    @Column(
            name = "roll_no",
            nullable = false
    )
    private String rollNumber;

    // Add cascade here so persisting Student automatically saves User first
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(
            name = "user_id",
            unique = true,
            nullable = false
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "hostel_no",
            nullable = false
    )
    private Hostel hostel;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "student"
    )
    private List<StudentDiet> diets;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "student"
    )
    private List<StudentExtra> extras;


    @Column(
            name = "created_at"
    )
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at"
    )
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
