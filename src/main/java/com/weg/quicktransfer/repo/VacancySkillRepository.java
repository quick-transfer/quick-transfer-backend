package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.VacancySkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface VacancySkillRepository extends JpaRepository<VacancySkill, UUID>,
        JpaSpecificationExecutor<VacancySkill> {

    Optional<VacancySkill> findFirstByNameIgnoreCase(String name);
}
