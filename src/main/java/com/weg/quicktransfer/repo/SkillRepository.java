package com.weg.quicktransfer.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Skill;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, UUID>, JpaSpecificationExecutor<Skill> {
    List<Skill> findByNameContainingIgnoreCase(String name);
}
