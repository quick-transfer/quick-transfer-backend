package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Vacancy;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface VacancyRepository extends JpaRepository<Vacancy, UUID>, JpaSpecificationExecutor<Vacancy> {

    @Query("SELECT v FROM Vacancy v WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Vacancy> findByName(@Param("name") String name);
}
