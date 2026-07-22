package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Manager;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    public Optional<Manager> findByName(String name);
}
