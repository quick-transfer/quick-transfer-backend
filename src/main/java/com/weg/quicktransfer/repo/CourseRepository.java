package com.weg.quicktransfer.repo;

import com.weg.quicktransfer.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {
    boolean existsByName(String name);

    Optional<Course> findFirstByName(String name);
}
