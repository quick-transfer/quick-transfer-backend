package com.weg.quicktransfer.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "date_time")
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "vacancy_id") 
    private Vacancy vacancy;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    public Interview(LocalDateTime dateTime, Vacancy vacancy, Place place, Manager manager) {
        this.dateTime = dateTime;
        this.vacancy = vacancy;
        this.place = place;
        this.manager = manager;
    }
}
