package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.coordinator.CoordinatorResponseDTO;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.CoordinatorMapper;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.repo.CoordinatorRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoordinatorService {

    private final CoordinatorRepository coordinatorRepository;
    private final CoordinatorMapper coordinatorMapper;

    public CoordinatorResponseDTO findById(Long id){
        if(id <= 0){
            throw new IllegalArgumentException("Id can not be less than 1");
        }
        Coordinator coordinator = coordinatorRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        List<String> coursesName = new ArrayList<>();

        for(Course course : coordinator.getCourses()){
            coursesName.add(course.getName());
        }

        return coordinatorMapper.toResponse(coordinator, coursesName);
    }
}
