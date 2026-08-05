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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Column(name = "reminder_sent", nullable = false)
    private Boolean reminderSent = false;

    @Column(name = "student_reminder_sent")
    private Boolean studentReminderSent = false;

    @Column(name = "coordinator_reminder_sent")
    private Boolean coordinatorReminderSent = false;

    @Column(name = "reminder_processing")
    private Boolean reminderProcessing = false;

    @Column(name = "reminder_claimed_at")
    private LocalDateTime reminderClaimedAt;

    public Interview(String interviewerName, LocalDateTime dateTime, Vacancy vacancy, Place place, Manager manager,
            Student student) {
        this.interviewerName = interviewerName;
        this.dateTime = dateTime;
        this.vacancy = vacancy;
        this.place = place;
        this.manager = manager;
        this.student = student;
    }
}
