package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Coordinator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CoordinatorRepository extends JpaRepository<Coordinator, UUID> {
    boolean existsByUsername(String username);

    List<Coordinator> findByUsername(String username);
}
