package com.weg.quicktransfer.model;

import com.weg.quicktransfer.enums.TransferRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shift_transfer_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftTransferRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_shift_id", nullable = false)
    private OperationalShift currentShift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_shift_id", nullable = false)
    private OperationalShift targetShift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferRequestStatus status = TransferRequestStatus.PENDING;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    void applyDefaults() {
        if (status == null) status = TransferRequestStatus.PENDING;
        if (requestedAt == null) requestedAt = LocalDateTime.now();
    }
}
