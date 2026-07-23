package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Manager;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    public List<Manager> findByName(String name);

    @Query("""
            SELECT m.name
            FROM Interview i
            JOIN i.manager m
            WHERE i.id = :interviewId
            """)
    public Optional<String> findByInterviewId(@Param("interviewId") Long interviewId);
}
