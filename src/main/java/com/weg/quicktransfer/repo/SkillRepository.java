package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>{
    
}
