package org.example.messmate.repository;

import org.example.messmate.entity.Student;
import org.example.messmate.entity.StudentExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StudentExtraRepository extends JpaRepository<StudentExtra, Long> {

    @Query("""
        SELECT se
        FROM StudentExtra se
        JOIN FETCH se.extra
        WHERE se.student = :student
          AND se.date BETWEEN :from AND :to
        """)
    List<StudentExtra> findForAnalysis(
            @Param("student") Student student,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
