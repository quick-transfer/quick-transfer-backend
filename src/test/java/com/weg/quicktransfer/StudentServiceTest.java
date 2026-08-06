package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.student.StudentRequestDTO;
import com.weg.quicktransfer.dto.student.StudentResponseDTO;
import com.weg.quicktransfer.dto.student.StudentUpdateRequestDTO;
import com.weg.quicktransfer.enums.StatusStudent;
import com.weg.quicktransfer.enums.StudentInterviewStatus;
import com.weg.quicktransfer.mapper.StudentMapper;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.ClassEntityRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import com.weg.quicktransfer.repo.OperationalShiftRepository;
import com.weg.quicktransfer.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private ClassEntityRepository classEntityRepository;

    @Mock
    private OperationalShiftRepository operationalShiftRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private StudentRequestDTO requestDTO;
    private StudentResponseDTO responseDTO;
    private ClassEntity classEntity;
    private Course course;

    private static final UUID STUDENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID CLASS_ID   = UUID.fromString("123e4567-e89b-12d3-a456-426614174111");
    private static final UUID COURSE_ID  = UUID.fromString("123e4567-e89b-12d3-a456-426614174222");

    @BeforeEach
    void setUp() {
        Coordinator coordinator = new Coordinator();
        course = new Course();
        course.setId(COURSE_ID);
        course.setName("Java");
        course.setCoordinator(coordinator);
        course.setClasses(new ArrayList<>());

        classEntity = new ClassEntity();
        classEntity.setId(CLASS_ID);
        classEntity.setCourse(course);
        classEntity.setFinishDate(LocalDate.now());
        classEntity.setStudents(new ArrayList<>());
        classEntity.setAcronym("JAVA01");

        student = new Student();
        student.setId(STUDENT_ID);
        student.setName("Nome");
        student.setEmail("email@dominio.com");
        student.setAge(17L);
        student.setAverageGrade(5.0);
        student.setClassEntity(classEntity);
        student.setStatus(StudentInterviewStatus.NOT_ASSOCIATED);
        student.setHasSeenEmail(false);

        requestDTO = new StudentRequestDTO("Nome", "email@dominio.com", 17L, 5.0, CLASS_ID, StudentInterviewStatus.NOT_ASSOCIATED.toString(), false);
        responseDTO = new StudentResponseDTO(STUDENT_ID, "Nome", "email@dominio.com", 17L, 5.0, classEntity.getAcronym(), course.getName(), StudentInterviewStatus.NOT_ASSOCIATED.toString(), false, StatusStudent.ENROLLED.toString());
    }

    @Test
    @DisplayName("Should create student and return response dto")
    void shouldCreateStudent() {
        when(classEntityRepository.findById(CLASS_ID)).thenReturn(Optional.of(classEntity));
        when(studentMapper.toEntity(requestDTO, classEntity)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toResponse(student)).thenReturn(responseDTO);

        StudentResponseDTO result = studentService.create(requestDTO);

        assertNotNull(result);
        assertEquals(STUDENT_ID, result.id());
        assertEquals("Nome", result.name());
        assertEquals("email@dominio.com", result.email());
        assertEquals(5.0, result.averageGrade());
        assertFalse(result.hasSeenEmail());

        verify(classEntityRepository).findById(CLASS_ID);
        verify(studentMapper).toEntity(requestDTO, classEntity);
        verify(studentRepository).save(student);
        verify(studentMapper).toResponse(student);
    }

    @Test
    @DisplayName("Should create multiple students and return response dtos")
    void shouldCreateMultipleStudents() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("[]".getBytes());

        when(file.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(
                same(inputStream),
                ArgumentMatchers.<TypeReference<List<StudentRequestDTO>>>any()
        )).thenReturn(List.of(requestDTO));
        when(classEntityRepository.findById(CLASS_ID)).thenReturn(Optional.of(classEntity));
        when(studentMapper.toEntity(requestDTO, classEntity)).thenReturn(student);
        when(studentRepository.saveAll(List.of(student))).thenReturn(List.of(student));
        when(studentMapper.toResponse(student)).thenReturn(responseDTO);

        List<StudentResponseDTO> result = studentService.createMultiple(file);

        assertEquals(List.of(responseDTO), result);
        verify(studentRepository).saveAll(List.of(student));
        verify(studentMapper).toResponse(student);
    }

    @Test
    @DisplayName("Should find student by id and return response dto")
    void shouldFindStudentById() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(studentMapper.toResponse(student)).thenReturn(responseDTO);

        StudentResponseDTO result = studentService.findById(STUDENT_ID);

        assertNotNull(result);
        assertEquals(STUDENT_ID, result.id());
        assertEquals("Nome", result.name());

        verify(studentRepository).findById(STUDENT_ID);
        verify(studentMapper).toResponse(student);
    }

    @Test
    @DisplayName("Should update student and return response dto")
    void shouldUpdateStudent() {
        // 8º argumento (statusStudent) estava faltando na chamada original — o record exige esse campo
        // porque o service acessa studentUpdateRequestDTO.statusStudent(). Passamos null pois o service
        // só aplica o campo quando ele não é nulo.
        StudentUpdateRequestDTO updatedRequest = new StudentUpdateRequestDTO(
                "Novo Nome", "novoemail@dominio.com", 18L, 8.5, CLASS_ID,
                StudentInterviewStatus.NOT_ASSOCIATED.toString(), false, null
        );

        Student updatedEntity = new Student();
        updatedEntity.setId(STUDENT_ID);
        updatedEntity.setName("Novo Nome");
        updatedEntity.setEmail("novoemail@dominio.com");
        updatedEntity.setAge(18L);
        updatedEntity.setAverageGrade(8.5);
        updatedEntity.setClassEntity(classEntity);
        updatedEntity.setStatus(StudentInterviewStatus.NOT_ASSOCIATED);
        updatedEntity.setHasSeenEmail(false);

        StudentResponseDTO updatedResponse = new StudentResponseDTO(STUDENT_ID, "Novo Nome", "novoemail@dominio.com", 18L, 8.5, "JAVA01", classEntity.getCourse().getName(), StudentInterviewStatus.NOT_ASSOCIATED.toString(), false, StatusStudent.ENROLLED.toString());

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(classEntityRepository.findById(CLASS_ID)).thenReturn(Optional.of(classEntity));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedEntity);
        when(studentMapper.toResponse(updatedEntity)).thenReturn(updatedResponse);

        StudentResponseDTO result = studentService.update(STUDENT_ID, updatedRequest);

        assertNotNull(result);
        assertEquals("Novo Nome", result.name());
        assertEquals("novoemail@dominio.com", result.email());
        assertEquals(8.5, result.averageGrade());

        verify(studentRepository).findById(STUDENT_ID);
        verify(classEntityRepository).findById(CLASS_ID);
        verify(studentRepository).save(any(Student.class));
        verify(studentMapper).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Should delete student")
    void shouldDeleteStudent() {
        when(studentRepository.existsById(STUDENT_ID)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(STUDENT_ID);

        assertDoesNotThrow(() -> studentService.delete(STUDENT_ID));

        verify(studentRepository).existsById(STUDENT_ID);
        verify(studentRepository).deleteById(STUDENT_ID);
    }

    @Test
    @DisplayName("Should mark email as read")
    void shouldMarkEmailAsRead() {
        StudentResponseDTO readResponse = new StudentResponseDTO(
                STUDENT_ID, "Nome", "email@dominio.com", 17L, 5.0,
                "JAVA01", classEntity.getCourse().getName(),
                StudentInterviewStatus.NOT_ASSOCIATED.toString(), true
                , StatusStudent.ENROLLED.toString()
        );

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toResponse(any(Student.class))).thenReturn(readResponse);

        StudentResponseDTO result = studentService.markEmailAsRead(STUDENT_ID);

        assertNotNull(result);
        assertTrue(result.hasSeenEmail());

        verify(studentRepository).findById(STUDENT_ID);
        verify(studentRepository).save(any(Student.class));
        verify(studentMapper).toResponse(any(Student.class));
    }
}
