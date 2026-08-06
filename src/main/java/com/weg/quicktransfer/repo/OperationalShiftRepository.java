package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.OperationalShift;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OperationalShiftRepository extends JpaRepository<OperationalShift, UUID> {
    Optional<OperationalShift> findByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT shift FROM OperationalShift shift WHERE shift.id = :id")
    Optional<OperationalShift> findByIdForUpdate(UUID id);
}
