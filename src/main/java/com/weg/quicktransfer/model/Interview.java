package com.weg.quicktransfer.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.weg.quicktransfer.enums.InterviewOutcome;
import com.weg.quicktransfer.enums.InterviewStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "interviews")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false, name = "interviewer_name")
    private String interviewerName;

    @Column(nullable = false, name = "date_time")
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacancy_id", nullable = false)
    private Vacancy vacancy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", unique = true)
    private VacancyApplication application;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewOutcome outcome = InterviewOutcome.PENDING;

    @Column(name = "reminder_sent", nullable = false)
    private Boolean reminderSent = false;

    public Interview(String interviewerName, LocalDateTime dateTime, Vacancy vacancy, Place place, Manager manager,
            Student student) {
        this.interviewerName = interviewerName;
        this.dateTime = dateTime;
        this.vacancy = vacancy;
        this.place = place;
        this.manager = manager;
        this.student = student;
        this.status = InterviewStatus.SCHEDULED;
        this.outcome = InterviewOutcome.PENDING;
    }
}
