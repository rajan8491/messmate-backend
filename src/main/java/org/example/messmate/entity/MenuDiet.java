package org.example.messmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.entity.keys.MenuDietId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "menu_diet")
public class MenuDiet {

    @EmbeddedId
    private MenuDietId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("menuPlanId")
    @JoinColumn(name = "menu_plan_id")
    private HostelMenuPlan menuPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dietId")
    @JoinColumn(name = "diet_id")
    private DietItem dietItem;

    @Column(name = "is_active",
            nullable = false
    )
    private Boolean active = true;

    @CreationTimestamp
    @Column(
            name = "created_at"
    )
    private LocalDateTime createdAt;


    @Column(
            name = "updated_at"
    )
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}