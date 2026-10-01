package org.example.messmate.repository.menuRepository;

import org.example.messmate.entity.ExtraItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ExtraItemRepository extends JpaRepository<ExtraItem, Long> {
    Collection<ExtraItem> findAllByIdIn(Set<Long> incomingExtraIds);
}
