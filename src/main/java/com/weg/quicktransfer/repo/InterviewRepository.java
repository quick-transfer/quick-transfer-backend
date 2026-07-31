package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.model.Interview;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, UUID>, JpaSpecificationExecutor<Interview> {
    List<Interview> findByDateTimeBetweenAndReminderSentFalse(LocalDateTime start, LocalDateTime end);

    boolean existsByStudent_Id(UUID studentId);

    boolean existsByStudent_IdAndIdNot(UUID studentId, UUID interviewId);

    long countByVacancy_Id(UUID vacancyId);

    long countByVacancy_IdAndIdNot(UUID vacancyId, UUID interviewId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Interview i
               SET i.reminderProcessing = true,
                   i.reminderClaimedAt = :claimedAt
             WHERE i.id = :id
               AND i.reminderSent = false
               AND (i.reminderProcessing = false
                    OR i.reminderClaimedAt IS NULL
                    OR i.reminderClaimedAt < :staleBefore)
            """)
    int claimReminder(
            @Param("id") UUID id,
            @Param("claimedAt") LocalDateTime claimedAt,
            @Param("staleBefore") LocalDateTime staleBefore);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Interview i
               SET i.reminderProcessing = false,
                   i.reminderClaimedAt = null
             WHERE i.id = :id
            """)
    void releaseReminderClaim(@Param("id") UUID id);
}
