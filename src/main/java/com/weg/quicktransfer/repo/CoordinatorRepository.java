package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Coordinator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordinatorRepository extends JpaRepository<Coordinator, Long> {
    boolean existsByUsername(String Username);
}
