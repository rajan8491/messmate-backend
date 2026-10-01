package org.example.messmate.repository.menuRepository;

import org.example.messmate.entity.HostelMenuPlan;
import org.example.messmate.entity.MessSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;

public interface HostelMenuPlanRepository extends JpaRepository<HostelMenuPlan, Long> {
    @Query("""
        SELECT DISTINCT s FROM HostelMenuPlan s
        JOIN FETCH s.messSlot ms
        WHERE s.hostelId = :hostelId
            AND ms.dayOfWeek = :dayOfWeek
    """)
    List<HostelMenuPlan> findSlotsByHostelAndDay(
            @Param("hostelId") Long hostelId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek
    );

    @Query("""
        SELECT DISTINCT s FROM HostelMenuPlan s
        JOIN FETCH s.messSlot ms
        WHERE s.hostelId = :hostelId
    """)
    List<HostelMenuPlan> findSlotsByHostel(
            @Param("hostelId") Long hostelId
    );
}
