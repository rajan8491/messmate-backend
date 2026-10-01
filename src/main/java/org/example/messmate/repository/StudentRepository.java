package org.example.messmate.repository;

import org.example.messmate.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllByHostelId(Long hostelId);

    void deleteAllByRollNumberIn(List<String> rollNos);
}
