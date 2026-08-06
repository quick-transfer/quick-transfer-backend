package com.weg.quicktransfer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import com.weg.quicktransfer.enums.StatusStudent;
import com.weg.quicktransfer.enums.StudentInterviewStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "students")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Student{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String registration;

    @Column(name = "attendance_rate", nullable = false)
    private Double attendanceRate = 0.0;

    @Column(nullable = false)
    private Long age;

    @Column(name = "average_grade")
    private Double averageGrade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentInterviewStatus status;
    
    @Column(nullable = false, name = "has_seen_email")
    private Boolean hasSeenEmail;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusStudent statusStudent;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classentity_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operational_shift_id")
    private OperationalShift operationalShift;

    @OneToMany(mappedBy = "student")
    private List<Interview> interviews = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Skill> skills = new ArrayList<>();

    public Student(String name, String email, Long age, Double averageGrade, ClassEntity classEntity,
            StudentInterviewStatus status, Boolean hasSeenEmail, Interview interview) {
        this.name = name;
        this.email = email;
        this.registration = defaultRegistration(email);
        this.age = age;
        this.averageGrade = averageGrade;
        this.classEntity = classEntity;
        this.status = status;
        this.hasSeenEmail = hasSeenEmail;
        this.statusStudent = StatusStudent.ENROLLED;
        if (interview != null) {
            this.interviews.add(interview);
        }
        this.attendanceRate = 0.0;
    }

    public Student(String name, String email, String registration, Double attendanceRate, Long age,
            Double averageGrade, ClassEntity classEntity, StudentInterviewStatus status,
            Boolean hasSeenEmail, Interview interview) {
        this.name = name;
        this.email = email;
        this.registration = registration;
        this.attendanceRate = attendanceRate;
        this.age = age;
        this.averageGrade = averageGrade;
        this.classEntity = classEntity;
        this.status = status;
        this.hasSeenEmail = hasSeenEmail;
        this.statusStudent = StatusStudent.ENROLLED;
        if (interview != null) {
            this.interviews.add(interview);
        }
    }

    @PrePersist
    void applyDefaults() {
        if (registration == null || registration.isBlank()) {
            registration = defaultRegistration(email);
        }
        if (attendanceRate == null) {
            attendanceRate = 0.0;
        }
    }

    private String defaultRegistration(String value) {
        String source = value == null ? UUID.randomUUID().toString() : value.trim().toLowerCase(Locale.ROOT);
        UUID stableId = UUID.nameUUIDFromBytes(source.getBytes(StandardCharsets.UTF_8));
        return "STU-" + stableId.toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
