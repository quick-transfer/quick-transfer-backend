package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.classEntity.ClassEntityResquestDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityResponseDTO;
import com.weg.quicktransfer.mapper.ClassEntityMapper;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.ClassEntityRepo;
import com.weg.quicktransfer.service.ClassEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassEntityServiceTest {

    @Mock
    private ClassEntityRepo classEntityRepo;

    @Mock
    private ClassEntityMapper classEntityMapper;

    @InjectMocks
    private ClassEntityService classEntityService;

    private ClassEntity classEntity;
    private ClassEntityRequestDTO requestDTO;
    private ClassEntityResponseDTO responseDTO;
    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);
        course.setName("Java");
        course.setClasses(new ArrayList<>());

        classEntity = new ClassEntity();
        classEntity.setId(1L);
        classEntity.setCourse(course);
        classEntity.setFinishDate(LocalDate.now());
        classEntity.setStudents(new ArrayList<Student>());
        classEntity.setAcronym("JAVA01");

        requestDTO = new ClassEntityRequestDTO();
        requestDTO.setCourseId(1L);
        requestDTO.setFinishDate(LocalDate.now());
        requestDTO.setAcronym("JAVA01");

        responseDTO = new ClassEntityResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCourseId(1L);
        responseDTO.setFinishDate(LocalDate.now());
        responseDTO.setAcronym("JAVA01");
    }

    @Test
    @DisplayName("Should create class entity and return response dto")
    void shouldCreateClassEntity() {
        when(classEntityMapper.toEntity(requestDTO)).thenReturn(classEntity);
        when(classEntityRepo.save(classEntity)).thenReturn(classEntity);
        when(classEntityMapper.toResponseDTO(classEntity)).thenReturn(responseDTO);

        ClassEntityResponseDTO result = classEntityService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("JAVA01", result.getAcronym());

        verify(classEntityMapper).toEntity(requestDTO);
        verify(classEntityRepo).save(classEntity);
        verify(classEntityMapper).toResponseDTO(classEntity);
    }

    @Test
    @DisplayName("Should find class entity by id and return response dto")
    void shouldFindClassEntityById() {
        when(classEntityRepo.findById(1L)).thenReturn(classEntity);
        when(classEntityMapper.toResponseDTO(classEntity)).thenReturn(responseDTO);

        ClassEntityResponseDTO result = classEntityService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("JAVA01", result.getAcronym());

        verify(classEntityRepo).findById(1L);
        verify(classEntityMapper).toResponseDTO(classEntity);
    }

    @Test
    @DisplayName("Should update class entity and return response dto")
    void shouldUpdateClassEntity() {
        ClassEntityRequestDTO updatedRequest = new ClassEntityRequestDTO();
        updatedRequest.setCourseId(1L);
        updatedRequest.setFinishDate(LocalDate.now().plusDays(30));
        updatedRequest.setAcronym("JAVA02");

        ClassEntity updatedEntity = new ClassEntity();
        updatedEntity.setId(1L);
        updatedEntity.setCourse(course);
        updatedEntity.setFinishDate(LocalDate.now().plusDays(30));
        updatedEntity.setStudents(new ArrayList<Student>());
        updatedEntity.setAcronym("JAVA02");

        ClassEntityResponseDTO updatedResponse = new ClassEntityResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setCourseId(1L);
        updatedResponse.setFinishDate(LocalDate.now().plusDays(30));
        updatedResponse.setAcronym("JAVA02");

        when(classEntityRepo.findById(1L)).thenReturn(classEntity);
        when(classEntityMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
        when(classEntityRepo.save(any(ClassEntity.class))).thenReturn(updatedEntity);
        when(classEntityMapper.toResponseDTO(updatedEntity)).thenReturn(updatedResponse);

        ClassEntityResponseDTO result = classEntityService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals("JAVA02", result.getAcronym());

        verify(classEntityRepo).findById(1L);
        verify(classEntityMapper).toEntity(updatedRequest);
        verify(classEntityRepo).save(any(ClassEntity.class));
        verify(classEntityMapper).toResponseDTO(updatedEntity);
    }

    @Test
    @DisplayName("Should delete class entity")
    void shouldDeleteClassEntity() {
        doNothing().when(classEntityRepo).deleteById(1L);

        assertDoesNotThrow(() -> classEntityService.delete(1L));

        verify(classEntityRepo).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> classEntityService.create(null));
    }
}