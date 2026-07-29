package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ClassEntityRepository extends JpaRepository<ClassEntity, UUID>, JpaSpecificationExecutor<ClassEntity> {
    boolean existsByAcronym(String acronym);

    Optional<ClassEntity> findFirstByAcronym(String acronym);
}
