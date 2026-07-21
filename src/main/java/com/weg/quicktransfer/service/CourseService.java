package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.course.CourseRequestDTO;
import com.weg.quicktransfer.dto.course.CourseResponseDTO;
import com.weg.quicktransfer.exception.CourseNotFoundException;
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.CoordinatorMapper;
import com.weg.quicktransfer.mapper.CourseMapper;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.repo.CoordinatorRepository;
import com.weg.quicktransfer.repo.CourseRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        //throws exception if a Course exists with that name
        if(courseRepository.existsByName(courseRequestDTO.name())){
            throw new CourseNotFoundException("Course already exists with this name");
        }
        //finds coordinator by id
        if(courseRequestDTO.coordinatorId() <= 0) {
            throw new IllegalArgumentException("Id can not be less than 1");
        }

        Coordinator coordinator = coordinatorRepository.findById(courseRequestDTO.coordinatorId()).orElseThrow(() -> new UserNotFoundException(courseRequestDTO.coordinatorId()));
        //initializes list
        List<String> coursesName = new ArrayList<>();

        //get courses names
        for(Course course : coordinator.getCourses()){
            coursesName.add(course.getName());
        }
        //transform CourseRequestDTO to entity
        Course course = courseMapper.toEntity(courseRequestDTO, coordinator);

        //saves course
        courseRepository.save(course);
        List<String> classesAcronym = new ArrayList<>();
        //returns CourseResponseDTO with empty list of class acronyms
        return courseMapper.toResponse(course, classesAcronym);
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> findAll(){
        //search all courses
        List<Course> courses = courseRepository.findAll();
        //initializes list
        List<CourseResponseDTO> courseResponseDTOS = new ArrayList<>();
        //goes through courses transforming each one to CourseResponseDTO with the list of class acronyms
        for(Course course : courses){
            List<String> classesAcronym = new ArrayList<>();
            //Creates a list of the class acronyms
            for (ClassEntity classEntity : course.getClasses()){
                classesAcronym.add(classEntity.getAcronym());
            }
            courseResponseDTOS.add(courseMapper.toResponse(course, classesAcronym));
        }
        return courseResponseDTOS;
    }

    @Transactional(readOnly = true)
    public CourseResponseDTO findById(Long id){
        return null;
    }
}
