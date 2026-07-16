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

        assertNotNull(found);
        assertEquals(validStudent.getId(), found.getId());
    }

    @Test
    @DisplayName("Should update student")
    public void updateStudent() {
        validStudent.setId(1L);

        Student updatedStudent = new Student(
                "Novo Nome",
                "novoemail@dominio.com",
                8.5,
                validStudent.getCurrentClass(),
                validStudent.getDesiredClass(),
                StudentInterviewStatus.ASSIGNED,
                false
        );

        updatedStudent.setId(1L);

        when(studentRepo.updateStudent(1L, updatedStudent))
                .thenReturn(Student);

        Student result = studentRepo.updateStudent(1L, updatedStudent);

        assertNotNull(result);
        assertEquals("Novo Nome", result.getName());
        assertEquals("novoemail@dominio.com", result.getEmail());
        assertEquals(8.5, result.getScore());

    }

    @Test
    @DisplayName("Should delete student")
    public void deleteStudent() {
        validStudent.setId(1L);

        doNothing().when(studentRepo).deleteStudent(1L);

        assertDoesNotThrow(() -> studentRepo.deleteStudent(1L));

    }

    @Test
    @DisplayName("Should mark email as read")
    public void markEmailAsRead() {
        validStudent.setId(1L);
        validStudent.setEmailRead(false);

        when(studentRepo.markEmailAsRead(1L))
                .thenAnswer(invocation -> {
                    validStudent.setEmailRead(true);
                    return validStudent;
                });

        Student result = studentRepo.markEmailAsRead(1L);

        assertTrue(result.isEmailRead());

    }
}