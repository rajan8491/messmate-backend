package org.example.messmate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.enums.Role;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter

@Entity
@Table(
        name = "users"
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(
            unique = true,
            nullable = false
    )
    private String username;

    @NotBlank
    @Column(
            nullable = false
    )
    private String password;

    @OneToOne(mappedBy = "user")
    private Accountant accountant;


    @OneToOne(mappedBy = "user")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private Boolean enabled = true;
}
