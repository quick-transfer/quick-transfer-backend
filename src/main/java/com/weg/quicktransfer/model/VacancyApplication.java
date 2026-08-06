package com.weg.quicktransfer.model;

import com.weg.quicktransfer.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "vacancy_applications",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_vacancy_application_student",
                columnNames = {"vacancy_id", "student_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VacancyApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacancy_id", nullable = false)
    private Vacancy vacancy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinator_id", nullable = false)
    private Coordinator coordinator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.REFERRED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "application")
    private Interview interview;

    public VacancyApplication(Vacancy vacancy, Student student, Coordinator coordinator, String notes) {
        this.vacancy = vacancy;
        this.student = student;
        this.coordinator = coordinator;
        this.notes = notes;
        this.status = ApplicationStatus.REFERRED;
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (status == null) {
            status = ApplicationStatus.REFERRED;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
