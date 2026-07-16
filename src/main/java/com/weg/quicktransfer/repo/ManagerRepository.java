package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    
}
