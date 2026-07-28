package com.weg.quicktransfer.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, UUID>{
    List<Skill> findByNameContaining(String name);
}
