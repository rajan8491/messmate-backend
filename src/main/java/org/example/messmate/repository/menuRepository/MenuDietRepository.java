package org.example.messmate.repository.menuRepository;

import org.example.messmate.entity.MenuDiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MenuDietRepository extends JpaRepository<MenuDiet, Long> {

    @Query("""
        SELECT md FROM MenuDiet md
        JOIN FETCH md.dietItem di
        WHERE md.menuPlan.id IN :menuPlanIds
            AND md.active = true
    """)
    Set<MenuDiet> findActiveDietsByMenuPlanIds(
            @Param("menuPlanIds") List<Long> menuPlanIds
    );
}
