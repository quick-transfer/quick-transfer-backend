package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByUsername(String username);
}
