package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Manager;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long>, JpaSpecificationExecutor<Manager> {
    public Optional<Manager> findByUsername(String username);

    @Query("""
            SELECT m
            FROM Interview i
            JOIN i.manager m
            WHERE i.id = :interviewId
            """)
    public Optional<Manager> findByInterviewId(@Param("interviewId") Long interviewId);
    
    List<Manager> findByName(String name);
}
