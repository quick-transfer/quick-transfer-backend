package com.weg.quicktransfer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.enums.ShiftClass;
import com.weg.quicktransfer.enums.StatusClass;

@Entity
@Table(name = "class_entities")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "finish_date", nullable = false)
    private LocalDate finishDate;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusClass status;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ShiftClass shiftClass;
    
    @Column(nullable = false, unique = true)
    private String acronym;

    @Column(nullable = false)
    private String name;

    @Column(name = "max_students", nullable = false)
    private Long maxStudents = 30L;

    @OneToMany(mappedBy = "classEntity", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Student> students = new ArrayList<>();

    @Version
    @Column(nullable = false)
    private long version;
    
    public ClassEntity(Course course, LocalDate startDate, LocalDate finishDate, StatusClass status,
            ShiftClass shiftClass, String acronym) {
        this.course = course;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.status = status;
        this.shiftClass = shiftClass;
        this.acronym = acronym;
        this.name = acronym;
        this.maxStudents = 30L;
    }

    public ClassEntity(Course course, LocalDate startDate, LocalDate finishDate, StatusClass status,
            ShiftClass shiftClass, String acronym, String name, Long maxStudents) {
        this.course = course;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.status = status;
        this.shiftClass = shiftClass;
        this.acronym = acronym;
        this.name = name;
        this.maxStudents = maxStudents;
    }

    @PrePersist
    void applyDefaults() {
        if (name == null || name.isBlank()) {
            name = acronym;
        }
        if (maxStudents == null) {
            maxStudents = 30L;
        }
    }
}
