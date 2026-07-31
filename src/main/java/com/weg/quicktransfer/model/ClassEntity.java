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

    @Version
    private Long version;

    @ManyToOne
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

    @OneToMany(mappedBy = "classEntity", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Student> students = new ArrayList<>();
    
    public ClassEntity(Course course, LocalDate startDate, LocalDate finishDate, StatusClass status,
            ShiftClass shiftClass, String acronym) {
        this.course = course;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.status = status;
        this.shiftClass = shiftClass;
        this.acronym = acronym;
    }
}
