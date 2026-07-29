package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID>, JpaSpecificationExecutor<Admin> {

    Optional<Admin> findFirstByUsername(String username);

    Optional<Admin> findFirstByName(String name);
}
