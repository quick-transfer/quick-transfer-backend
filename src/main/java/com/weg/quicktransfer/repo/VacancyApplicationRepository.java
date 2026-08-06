package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.VacancyApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VacancyApplicationRepository extends JpaRepository<VacancyApplication, UUID> {
    boolean existsByVacancyIdAndStudentId(UUID vacancyId, UUID studentId);

    Page<VacancyApplication> findAllByCoordinatorId(UUID coordinatorId, Pageable pageable);

    Page<VacancyApplication> findAllByVacancyManagerId(UUID managerId, Pageable pageable);
}
