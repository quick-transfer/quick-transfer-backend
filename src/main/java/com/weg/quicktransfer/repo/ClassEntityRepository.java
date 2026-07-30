package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClassEntityRepository extends JpaRepository<ClassEntity, UUID>, JpaSpecificationExecutor<ClassEntity> {
    boolean existsByAcronym(String acronym);

    @Query("SELECT c FROM ClassEntity c WHERE LOWER(c.acronym) LIKE LOWER(CONCAT('%', :acronym, '%'))")
    List<ClassEntity> findByAcronym(@Param("keyword") String acronym);
}
