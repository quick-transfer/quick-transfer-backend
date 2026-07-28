package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClassEntityRepository extends JpaRepository<ClassEntity, UUID> {
    boolean existsByAcronym(String acronym);

    Optional<ClassEntity> findByAcronym(String acronym);
}
