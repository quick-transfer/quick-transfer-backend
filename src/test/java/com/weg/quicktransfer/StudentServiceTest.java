package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.StudentRequestDTO;
import com.weg.quicktransfer.dto.StudentResponseDTO;
import com.weg.quicktransfer.mapper.StudentMapper;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.model.StudentInterviewStatus;
import com.weg.quicktransfer.repo.StudentRepo;
import com.weg.quicktransfer.service.StudentService;
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
class StudentServiceTest {

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private StudentRequestDTO requestDTO;
    private StudentResponseDTO responseDTO;
    private ClassEntity classEntity;

    @BeforeEach
    void setUp() {
        Coordinator coordinator = new Coordinator();
        Course course = new Course();
        course.setId(1L);
        course.setName("Java");
        course.setCoordinator(coordinator);
        course.setClasses(new ArrayList<>());

        classEntity = new ClassEntity();
        classEntity.setId(1L);
        classEntity.setCourse(course);
        classEntity.setFinishDate(LocalDate.now());
        classEntity.setStudents(new ArrayList<>());
        classEntity.setAcronym("JAVA01");

        student = new Student();
        student.setId(1L);
        student.setName("Nome");
        student.setEmail("email@dominio.com");
        student.setAverageGrade(5.0);
        student.setClassEntity(classEntity);
        student.setDesiredClass(classEntity);
        student.setStatus(StudentInterviewStatus.NAO_ASSOCIADO);
        student.setHasSeenEmail(false);

        requestDTO = new StudentRequestDTO();
        requestDTO.setName("Nome");
        requestDTO.setEmail("email@dominio.com");
        requestDTO.setAverageGrade(5.0);
        requestDTO.setClassEntityId(1L);
        requestDTO.setDesiredClassId(1L);
        requestDTO.setStatus(StudentInterviewStatus.NAO_ASSOCIADO);
        requestDTO.setHasSeenEmail(false);

        responseDTO = new StudentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Nome");
        responseDTO.setEmail("email@dominio.com");
        responseDTO.setAverageGrade(5.0);
        responseDTO.setClassEntityId(1L);
        responseDTO.setDesiredClassId(1L);
        responseDTO.setStatus(StudentInterviewStatus.NAO_ASSOCIADO);
        responseDTO.setHasSeenEmail(false);
    }

    @Test
    @DisplayName("Should create student and return response dto")
    void shouldCreateStudent() {
        when(studentMapper.toEntity(requestDTO)).thenReturn(student);
        when(studentRepo.save(student)).thenReturn(student);
        when(studentMapper.toResponseDTO(student)).thenReturn(responseDTO);

        StudentResponseDTO result = studentService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Nome", result.getName());
        assertEquals("email@dominio.com", result.getEmail());
        assertEquals(5.0, result.getAverageGrade());
        assertFalse(result.isHasSeenEmail());

        verify(studentMapper).toEntity(requestDTO);
        verify(studentRepo).save(student);
        verify(studentMapper).toResponseDTO(student);
    }

    @Test
    @DisplayName("Should find student by id and return response dto")
    void shouldFindStudentById() {
        when(studentRepo.findById(1L)).thenReturn(student);
        when(studentMapper.toResponseDTO(student)).thenReturn(responseDTO);

        StudentResponseDTO result = studentService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Nome", result.getName());

        verify(studentRepo).findById(1L);
        verify(studentMapper).toResponseDTO(student);
    }

    @Test
    @DisplayName("Should update student and return response dto")
    void shouldUpdateStudent() {
        StudentRequestDTO updatedRequest = new StudentRequestDTO();
        updatedRequest.setName("Nome Atualizado");
        updatedRequest.setEmail("novoemail@dominio.com");
        updatedRequest.setAverageGrade(8.0);
        updatedRequest.setClassEntityId(1L);
        updatedRequest.setDesiredClassId(1L);
        updatedRequest.setStatus(StudentInterviewStatus.VISTO);
        updatedRequest.setHasSeenEmail(true);

        Student updatedEntity = new Student();
        updatedEntity.setId(1L);
        updatedEntity.setName("Nome Atualizado");
        updatedEntity.setEmail("novoemail@dominio.com");
        updatedEntity.setAverageGrade(8.0);
        updatedEntity.setClassEntity(classEntity);
        updatedEntity.setDesiredClass(classEntity);
        updatedEntity.setStatus(StudentInterviewStatus.VISTO);
        updatedEntity.setHasSeenEmail(true);

        StudentResponseDTO updatedResponse = new StudentResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Nome Atualizado");
        updatedResponse.setEmail("novoemail@dominio.com");
        updatedResponse.setAverageGrade(8.0);
        updatedResponse.setClassEntityId(1L);
        updatedResponse.setDesiredClassId(1L);
        updatedResponse.setStatus(StudentInterviewStatus.VISTO);
        updatedResponse.setHasSeenEmail(true);

        when(studentRepo.findById(1L)).thenReturn(student);
        when(studentMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
        when(studentRepo.save(any(Student.class))).thenReturn(updatedEntity);
        when(studentMapper.toResponseDTO(updatedEntity)).thenReturn(updatedResponse);

        StudentResponseDTO result = studentService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals("Nome Atualizado", result.getName());
        assertEquals("novoemail@dominio.com", result.getEmail());
        assertEquals(8.0, result.getAverageGrade());
        assertTrue(result.isHasSeenEmail());

        verify(studentRepo).findById(1L);
        verify(studentMapper).toEntity(updatedRequest);
        verify(studentRepo).save(any(Student.class));
        verify(studentMapper).toResponseDTO(updatedEntity);
    }

    @Test
    @DisplayName("Should mark interview email as read")
    void shouldMarkInterviewEmailAsRead() {
        student.setHasSeenEmail(false);

        Student seenStudent = new Student();
        seenStudent.setId(1L);
        seenStudent.setName("Nome");
        seenStudent.setEmail("email@dominio.com");
        seenStudent.setAverageGrade(5.0);
        seenStudent.setClassEntity(classEntity);
        seenStudent.setDesiredClass(classEntity);
        seenStudent.setStatus(StudentInterviewStatus.VISTO);
        seenStudent.setHasSeenEmail(true);

        StudentResponseDTO seenResponse = new StudentResponseDTO();
        seenResponse.setId(1L);
        seenResponse.setName("Nome");
        seenResponse.setEmail("email@dominio.com");
        seenResponse.setAverageGrade(5.0);
        seenResponse.setClassEntityId(1L);
        seenResponse.setDesiredClassId(1L);
        seenResponse.setStatus(StudentInterviewStatus.VISTO);
        seenResponse.setHasSeenEmail(true);

        when(studentRepo.findById(1L)).thenReturn(student);
        when(studentRepo.save(any(Student.class))).thenReturn(seenStudent);
        when(studentMapper.toResponseDTO(seenStudent)).thenReturn(seenResponse);

        StudentResponseDTO result = studentService.seeInterviewEmail(1L);

        assertNotNull(result);
        assertTrue(result.isHasSeenEmail());

        verify(studentRepo).findById(1L);
        verify(studentRepo).save(any(Student.class));
        verify(studentMapper).toResponseDTO(seenStudent);
    }

    @Test
    @DisplayName("Should delete student")
    void shouldDeleteStudent() {
        doNothing().when(studentRepo).deleteById(1L);

        assertDoesNotThrow(() -> studentService.delete(1L));

        verify(studentRepo).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> studentService.create(null));
    }
}