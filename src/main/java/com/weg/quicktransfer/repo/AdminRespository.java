package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AdminRespository extends JpaRepository<Admin, Long> {
    boolean existsByUsername(String username);
}
