package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("""
            SELECT s
            FROM Student s
            WHERE s.interview = :interviewId""")
    public Optional<Student> findByInterviewId(@Param("interviewId") Long interviewId);

    public List<Student> findByName(String name);
}
