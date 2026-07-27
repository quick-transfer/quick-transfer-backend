package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Coordinator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CoordinatorRepository extends JpaRepository<Coordinator, Long>, JpaSpecificationExecutor<Coordinator> {
    boolean existsByUsername(String username);

    List<Coordinator> findByUsername(String username);
}
