package com.weg.quicktransfer.model;

import java.util.ArrayList;
import java.util.List;

import com.weg.quicktransfer.enums.Shift;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vacancies")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Shift shift;

    @Column(nullable = false)
    private Place place;

    @OneToMany(mappedBy = "vacancy")
    private List<Interview> interviews = new ArrayList<>();

    public Vacancy(Shift shift, Place place) {
        this.shift = shift;
        this.place = place;
    }
}