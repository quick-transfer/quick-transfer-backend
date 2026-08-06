package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.enums.TransferRequestStatus;
import com.weg.quicktransfer.model.ShiftTransferRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShiftTransferRequestRepository extends JpaRepository<ShiftTransferRequest, UUID> {
    Page<ShiftTransferRequest> findAllByStatus(TransferRequestStatus status, Pageable pageable);
    List<ShiftTransferRequest> findAllByStudentIdOrderByRequestedAtDesc(UUID studentId);
    boolean existsByStudentIdAndStatus(UUID studentId, TransferRequestStatus status);
}
