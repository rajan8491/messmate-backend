package org.example.messmate.repository.menuRepository;

import org.example.messmate.entity.MenuExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface MenuExtraRepository extends JpaRepository<MenuExtra, Long> {
//    @Query("""
//        SELECT me FROM MenuExtra me
//        JOIN FETCH me.extraItem e
//        WHERE me.menuPlan.id = :menuPlanId
//          AND CAST(me.createdAt AS LocalDate) <= :date
//        ORDER BY me.createdAt DESC
//    """)
//    List<MenuExtra> findExtrasByDate(
//            @Param("menuPlanId") Long menuPlanId,
//            @Param("date") LocalDate date
//    );

    @Query("""
        SELECT me FROM MenuExtra me
        JOIN FETCH me.extraItem e
        WHERE me.menuPlan.id = :menuPlanId
          AND me.active = true
    """)
    List<MenuExtra> findExtras(
            @Param("menuPlanId") Long menuPlanId
    );

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
