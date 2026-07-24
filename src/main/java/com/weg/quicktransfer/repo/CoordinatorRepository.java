package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Coordinator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoordinatorRepository extends JpaRepository<Coordinator, Long> {
    boolean existsByUsername(String username);

    List<Coordinator> findByUsername(String username);
}
