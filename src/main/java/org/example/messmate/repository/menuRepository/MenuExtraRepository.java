package org.example.messmate.repository.menuRepository;

import org.example.messmate.entity.MenuExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MenuExtraRepository extends JpaRepository<MenuExtra, Long> {
    @Query("""
        SELECT me FROM MenuExtra me
        JOIN FETCH me.extraItem ei
        WHERE me.menuPlan.id IN :menuPlanIds
            AND me.active = true
    """)
    Set<MenuExtra> findActiveExtrasByMenuPlanIds(
            @Param("menuPlanIds") List<Long> menuPlanIds
    );
}
