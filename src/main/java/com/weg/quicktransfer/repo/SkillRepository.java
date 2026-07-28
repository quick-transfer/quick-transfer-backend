package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Skill;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {
    public Optional<Skill> findByName(String name);
}
