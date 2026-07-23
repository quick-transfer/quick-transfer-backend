package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByName(String name);

    Optional<Course> findByName(String name);
}
