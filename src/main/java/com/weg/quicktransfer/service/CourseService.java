package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.course.CourseRequestDTO;
import com.weg.quicktransfer.dto.course.CourseResponseDTO;
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.mapper.CourseMapper;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.repo.CourseRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final CoordinatorService coordinatorService;

    public CourseResponseDTO create(CourseRequestDTO courseRequestDTO){
        if(courseRepository.existsByName(courseRequestDTO.name())){
            throw new CourseNotFoundException("Course already exists with this name");
        }
        Coordinator coordinator = coordinatorService.findById(courseRequestDTO.coordinatorId());

        Course course = courseMapper.toEntity(courseRequestDTO, coordinator);

        courseRepository.save(course);
        List<String> classesAcronym = new ArrayList<>();
        return courseMapper.toResponse(course, classesAcronym);
    }

    public List<CourseResponseDTO> findAll(){
        List<Course> courses = courseRepository.findAll();
        List<CourseResponseDTO> courseResponseDTOS = new ArrayList<>();
        for(Course course : courses){
            List<String> classesAcronym = new ArrayList<>();
            for (ClassEntity classEntity : course.getClasses()){
                classesAcronym.add(classEntity.getAcronym());
            }
            courseResponseDTOS.add(courseMapper.toResponse(course, classesAcronym));
        }
        return courseResponseDTOS;
    }
}
