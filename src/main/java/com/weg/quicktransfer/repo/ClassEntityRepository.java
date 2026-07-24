package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {
    boolean existsByAcronym(String acronym);

    Optional<ClassEntity> findByAcronym(String acronym);
}
