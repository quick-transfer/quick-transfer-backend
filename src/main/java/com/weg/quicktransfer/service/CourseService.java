package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.course.CourseFilter;
import com.weg.quicktransfer.dto.course.CourseRequestDTO;
import com.weg.quicktransfer.dto.course.CourseResponseDTO;
import com.weg.quicktransfer.dto.course.CourseUpdateRequestDTO;
import com.weg.quicktransfer.exception.CoordinatorNotFoundException;
import com.weg.quicktransfer.exception.CourseNotFoundException;
import com.weg.quicktransfer.mapper.CourseMapper;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.repo.CoordinatorRepository;
import com.weg.quicktransfer.repo.CourseRepository;
import com.weg.quicktransfer.repo.specifications.CourseSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final CoordinatorRepository coordinatorRepository;

    @Transactional
    public CourseResponseDTO create(CourseRequestDTO courseRequestDTO){
        Coordinator coordinator = coordinatorRepository.findById(courseRequestDTO.coordinatorId()).orElseThrow(() -> new CoordinatorNotFoundException(courseRequestDTO.coordinatorId()));

        Course course = courseMapper.toEntity(courseRequestDTO, coordinator);

        course = courseRepository.save(course);

        return courseMapper.toResponse(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> findAll(){
        List<Course> courses = courseRepository.findAll();

        return courses.stream().map(courseMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CourseResponseDTO findById(UUID id){
        Course course = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));

        return courseMapper.toResponse(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> findByName(String name){
        List<Course> courses = courseRepository.findByNameContaining(name);

        return courses.stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> searchCourses(CourseFilter filter) {
        Specification<Course> spec = CourseSpecification.getFilteredCourses(filter);

        List<Course> courses = courseRepository.findAll(spec);

        return courses.stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Transactional
    public CourseResponseDTO update(UUID id, CourseUpdateRequestDTO courseUpdateRequestDTO){
        Course course = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));

        if(courseUpdateRequestDTO.name() != null && !courseUpdateRequestDTO.name().isBlank()) {
            course.setName(courseUpdateRequestDTO.name());
        }

        if(courseUpdateRequestDTO.coordinatorId() != null) {
            Coordinator coordinator = coordinatorRepository.findById(courseUpdateRequestDTO.coordinatorId()).orElseThrow(() -> new CoordinatorNotFoundException(courseUpdateRequestDTO.coordinatorId()));
            course.setCoordinator(coordinator);
        }

        Course courseAtt = courseRepository.save(course);

        return courseMapper.toResponse(courseAtt);
    }

    @Transactional
    public void delete(UUID id){
        if(!courseRepository.existsById(id)) {
            throw new CourseNotFoundException(id);
        }

        courseRepository.deleteById(id);
    }
}
