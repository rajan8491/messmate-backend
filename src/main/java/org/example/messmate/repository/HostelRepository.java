package org.example.messmate.repository;

import org.example.messmate.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostelRepository extends JpaRepository<Hostel,Long> {
    //
}
