package com.weg.quicktransfer.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.weg.quicktransfer.model.Interview;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.weg.quicktransfer.enums.InterviewOutcome;
import com.weg.quicktransfer.enums.InterviewStatus;

public interface InterviewRepository extends JpaRepository<Interview, UUID>, JpaSpecificationExecutor<Interview> {
     @Query("""
            select i.id from Interview i
            where i.reminderSent = false
              and i.dateTime between :now and :deadline
            order by i.dateTime
            """)
    List<UUID> findPendingReminderIds(
            LocalDateTime now,
            LocalDateTime deadline,
            Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Interview i where i.id = :id")
    Optional<Interview> findByIdForUpdate(UUID id);

    boolean existsByStudent_Id(UUID studentId);

    boolean existsByStudent_IdAndIdNot(UUID studentId, UUID interviewId);

    long countByVacancy_Id(UUID vacancyId);

    long countByVacancy_IdAndIdNot(UUID vacancyId, UUID interviewId);

    boolean existsByStudentIdAndStatus(UUID studentId, InterviewStatus status);

    boolean existsByStudentIdAndStatusAndIdNot(UUID studentId, InterviewStatus status, UUID interviewId);

    long countByVacancyIdAndOutcome(UUID vacancyId, InterviewOutcome outcome);

    Page<Interview> findAllByManagerId(UUID managerId, Pageable pageable);

    List<Interview> findAllByStudentIdOrderByDateTimeDesc(UUID studentId);

}
