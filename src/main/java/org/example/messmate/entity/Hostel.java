package org.example.messmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.enums.Residents;

@Getter
@Setter

@Entity
public class Hostel {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Residents residents;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "accountant_id"
    )
    private Accountant accountant;


    private String email;

    private String phone;

    private String description;
}
