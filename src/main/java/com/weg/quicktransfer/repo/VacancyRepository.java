package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Vacancy;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    
}
