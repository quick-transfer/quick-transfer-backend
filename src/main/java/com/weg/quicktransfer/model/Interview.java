package com.weg.quicktransfer.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    @Column(nullable = false, name = "interviewer_name")
    private String interviewerName;
    
    @Column(nullable = false, name = "date_time")
    private LocalDateTime dateTime;
    
    @ManyToOne
    @JoinColumn(name = "vacancy_id", nullable = false) 
    private Vacancy vacancy;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;

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
