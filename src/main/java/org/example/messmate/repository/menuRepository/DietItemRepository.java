package org.example.messmate.repository.menuRepository;

import org.example.messmate.entity.DietItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface DietItemRepository extends JpaRepository<DietItem, Long> {
    Collection<DietItem> findAllByIdIn(Set<Long> incomingDietIds);
}
