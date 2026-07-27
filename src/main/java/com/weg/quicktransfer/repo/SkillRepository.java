package com.weg.quicktransfer.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>{
    List<Skill> findByNameContaining(String name);
}
