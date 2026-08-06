package com.weg.quicktransfer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Locale;

import com.weg.quicktransfer.enums.EntityStatus;

@Entity
@Table(name = "courses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityStatus status = EntityStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinator_id", nullable = false)
    private Coordinator coordinator;

    @OneToMany(mappedBy = "course", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ClassEntity> classes = new ArrayList<>();

    public Course(String name, Coordinator coordinator, List<ClassEntity> classes) {
        this.name = name;
        this.code = defaultCode(name);
        this.coordinator = coordinator;
        this.classes = classes;
        this.status = EntityStatus.ACTIVE;
    }

    public Course(String name, Coordinator coordinator) {
        this.name = name;
        this.code = defaultCode(name);
        this.coordinator = coordinator;
        this.status = EntityStatus.ACTIVE;
    }

    public Course(String name, String code, EntityStatus status, Coordinator coordinator) {
        this.name = name;
        this.code = code;
        this.status = status;
        this.coordinator = coordinator;
    }

    @PrePersist
    void applyDefaults() {
        if (code == null || code.isBlank()) {
            code = defaultCode(name);
        }
        if (status == null) {
            status = EntityStatus.ACTIVE;
        }
    }

    private String defaultCode(String value) {
        String normalized = value == null ? "COURSE" : value.trim().toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "-").replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? "COURSE" : normalized;
    }
}
