package org.example.messmate.repository;

import org.example.messmate.entity.Accountant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountantRepository extends JpaRepository<Accountant, Long> {
}
