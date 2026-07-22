package com.weg.quicktransfer.service;

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
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final CoordinatorRepository coordinatorRepository;

    @Transactional
    //returns a new Course
    public CourseResponseDTO create(CourseRequestDTO courseRequestDTO){
        Coordinator coordinator = coordinatorRepository.findById(courseRequestDTO.coordinatorId()).orElseThrow(() -> new CoordinatorNotFoundException(courseRequestDTO.coordinatorId()));

        Course course = courseMapper.toEntity(courseRequestDTO, coordinator);

        courseRepository.save(course);

        return courseMapper.toResponse(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> findAll(){
        List<Course> courses = courseRepository.findAll();

        return courses.stream().map(courseMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CourseResponseDTO findById(Long id){
        Course course = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));

        return courseMapper.toResponse(course);
    }

    @Transactional
    public CourseResponseDTO update(Long id, String name, Long coordinatorId){
        Course course = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));

        Coordinator coordinator = coordinatorRepository.findById(id).orElseThrow(() -> new CoordinatorNotFoundException(coordinatorId));

        if(StringUtils.hasText(name)) {
            course.setName(name);
        }

        if(coordinatorId != null && coordinatorId > 0) {
            course.setCoordinator(coordinator);
        }

        Course courseAtt = courseRepository.save(course);

        return courseMapper.toResponse(courseAtt);
    }

    @Transactional
    public void delete(Long id){
        if(!courseRepository.existsById(id)) {
            throw new CourseNotFoundException(id);
        }

        courseRepository.deleteById(id);
    }
}
