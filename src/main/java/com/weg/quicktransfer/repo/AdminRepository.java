package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByUsername(String username);

    Admin findByUsername(String username);

    List<Admin> findByNameContaining(String name);
}
