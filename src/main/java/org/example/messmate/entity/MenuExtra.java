package org.example.messmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.entity.keys.MenuExtraId;

@Getter
@Setter
@Entity
@Table(name = "menu_extra")
public class MenuExtra {

    @EmbeddedId
    private MenuExtraId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("menuPlanId")
    @JoinColumn(name = "menu_plan_id")
    private HostelMenuPlan menuPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("extraId")
    @JoinColumn(name = "extra_id")
    private ExtraItem extraItem;

    @Column(name = "is_active",
            nullable = false
    )
    private Boolean active = true;
}