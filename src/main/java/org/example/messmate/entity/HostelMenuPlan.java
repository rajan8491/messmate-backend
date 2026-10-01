package org.example.messmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(
        name = "hostel_menu_plan",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "hostel_id",
                                "mess_slot_id"
                        }
                )
        }
)
public class HostelMenuPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "hostel_id",
            nullable = false
    )
    private Long hostelId;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "mess_slot_id",
            nullable = false
    )
    private MessSlot messSlot;


    @OneToMany(
            mappedBy = "menuPlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<MenuDiet> diets = new HashSet<>();

    @OneToMany(
            mappedBy = "menuPlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<MenuExtra> extras = new HashSet<>();

}
