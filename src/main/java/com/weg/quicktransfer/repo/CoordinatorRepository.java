package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Coordinator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CoordinatorRepository extends JpaRepository<Coordinator, UUID>, JpaSpecificationExecutor<Coordinator> {

    @Query("SELECT c FROM Coordinator c WHERE LOWER(c.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Coordinator> searchUsersByName(@Param("keyword") String keyword);
}
