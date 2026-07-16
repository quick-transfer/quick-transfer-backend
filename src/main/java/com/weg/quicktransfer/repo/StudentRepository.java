package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    
}
