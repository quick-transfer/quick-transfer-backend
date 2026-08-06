package com.weg.quicktransfer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettings {
    @Id
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "default_shift_capacity", nullable = false)
    private Integer defaultShiftCapacity;

    @Column(name = "high_demand_percentage", nullable = false)
    private Integer highDemandPercentage;

    @Column(name = "email_sender", nullable = false)
    private String emailSender;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = LocalDateTime.now();
    }
}
