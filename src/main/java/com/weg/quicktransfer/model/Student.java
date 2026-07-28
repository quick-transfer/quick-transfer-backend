package com.weg.quicktransfer.model;

import java.util.ArrayList;
import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

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
    private StatusStudent statusStudent;
    
    @ManyToOne
    @JoinColumn(name = "classentity_id", nullable = false)
    private ClassEntity classEntity;

    @OneToOne(mappedBy = "student")
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @OneToMany(mappedBy = "student")
    private List<Skill> skills = new ArrayList<>();

    public Student(String name, String email, Long age, Double averageGrade, ClassEntity classEntity,
            StudentInterviewStatus status, Boolean hasSeenEmail, Interview interview) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.averageGrade = averageGrade;
        this.classEntity = classEntity;
        this.status = status;
        this.hasSeenEmail = hasSeenEmail;
        this.statusStudent = StatusStudent.CURSANDO;
        this.interview = interview;
    }
}
