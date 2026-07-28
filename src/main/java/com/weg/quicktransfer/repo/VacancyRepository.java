package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Vacancy;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long>, JpaSpecificationExecutor<Vacancy> {
    Optional<Vacancy> findByName(String name);
}
