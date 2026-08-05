package com.weg.quicktransfer.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.model.Interview;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
