package org.example.messmate.repository.menuRepository;

import org.example.messmate.entity.HostelMenuPlan;
import org.example.messmate.entity.MessSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface HostelMenuPlanRepository extends JpaRepository<HostelMenuPlan, Long> {
    Optional<HostelMenuPlan> findByHostelIdAndMessSlotId(Long hostelId, Long messSlotId);

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
