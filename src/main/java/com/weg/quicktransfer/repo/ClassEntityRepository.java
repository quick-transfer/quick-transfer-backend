package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long>, JpaSpecificationExecutor<ClassEntity> {
    boolean existsByAcronym(String acronym);

    Optional<ClassEntity> findByAcronym(String acronym);
}
