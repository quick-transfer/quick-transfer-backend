// package com.weg.quicktransfer;

// import com.weg.quicktransfer.dto.student.StudentRequestDTO;
// import com.weg.quicktransfer.dto.student.StudentResponseDTO;
// import com.weg.quicktransfer.enums.StudentInterviewStatus;
// import com.weg.quicktransfer.mapper.StudentMapper;
// import com.weg.quicktransfer.model.ClassEntity;
// import com.weg.quicktransfer.model.Coordinator;
// import com.weg.quicktransfer.model.Course;
// import com.weg.quicktransfer.model.Student;
// import com.weg.quicktransfer.repo.StudentRepository;
// import com.weg.quicktransfer.service.StudentService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class StudentServiceTest {

//     @Mock
//     private StudentRepository studentRepository;

//     @Mock
//     private StudentMapper studentMapper;

//     @InjectMocks
//     private StudentService studentService;

//     private Student student;
//     private StudentRequestDTO requestDTO;
//     private StudentResponseDTO responseDTO;
//     private ClassEntity classEntity;

//     @BeforeEach
//     void setUp() {
//         Coordinator coordinator = new Coordinator();
//         Course course = new Course();
//         course.setId(1L);
//         course.setName("Java");
//         course.setCoordinator(coordinator);
//         course.setClasses(new ArrayList<>());

//         classEntity = new ClassEntity();
//         classEntity.setId(1L);
//         classEntity.setCourse(course);
//         classEntity.setFinishDate(LocalDate.now());
//         classEntity.setStudents(new ArrayList<>());
//         classEntity.setAcronym("JAVA01");

//         student = new Student();
//         student.setId(1L);
//         student.setName("Nome");
//         student.setEmail("email@dominio.com");
//         student.setAverageGrade(5.0);
//         student.setClassEntity(classEntity);
//         student.setDesiredClass(classEntity);
//         student.setStatus(StudentInterviewStatus.NAO_ASSOCIADO);
//         student.setHasSeenEmail(false);

//         // Inicialização utilizando os construtores canônicos dos Records
//         requestDTO = new StudentRequestDTO("Nome", "email@dominio.com", 5.0, 1L, 1L, StudentInterviewStatus.NAO_ASSOCIADO, false);
//         responseDTO = new StudentResponseDTO(1L, "Nome", "email@dominio.com", 5.0, 1L, 1L, StudentInterviewStatus.NAO_ASSOCIADO, false);
//     }

//     @Test
//     @DisplayName("Should create student and return response dto")
//     void shouldCreateStudent() {
//         when(studentMapper.toEntity(requestDTO)).thenReturn(student);
//         when(studentRepository.save(student)).thenReturn(student);
//         when(studentMapper.toResponse(student)).thenReturn(responseDTO);

//         StudentResponseDTO result = studentService.create(requestDTO);

//         assertNotNull(result);
//         assertEquals(1L, result.id());
//         assertEquals("Nome", result.name());
//         assertEquals("email@dominio.com", result.email());
//         assertEquals(5.0, result.averageGrade());
//         assertFalse(result.hasSeenEmail());

//         verify(studentMapper).toEntity(requestDTO);
//         verify(studentRepository).save(student);
//         verify(studentMapper).toResponse(student);
//     }

//     @Test
//     @DisplayName("Should find student by id and return response dto")
//     void shouldFindStudentById() {
//         when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
//         when(studentMapper.toResponse(student)).thenReturn(responseDTO);

//         StudentResponseDTO result = studentService.findById(1L);

//         assertNotNull(result);
//         assertEquals(1L, result.id());
//         assertEquals("Nome", result.name());

//         verify(studentRepository).findById(1L);
//         verify(studentMapper).toResponse(student);
//     }

//     @Test
//     @DisplayName("Should update student and return response dto")
//     void shouldUpdateStudent() {
//         StudentRequestDTO updatedRequest = new StudentRequestDTO("Novo Nome", "novoemail@dominio.com", 8.5, 1L, 1L, StudentInterviewStatus.NAO_ASSOCIADO, false);

//         Student updatedEntity = new Student();
//         updatedEntity.setId(1L);
//         updatedEntity.setName("Novo Nome");
//         updatedEntity.setEmail("novoemail@dominio.com");
//         updatedEntity.setAverageGrade(8.5);
//         updatedEntity.setClassEntity(classEntity);
//         updatedEntity.setDesiredClass(classEntity);
//         updatedEntity.setStatus(StudentInterviewStatus.NAO_ASSOCIADO);
//         updatedEntity.setHasSeenEmail(false);

//         StudentResponseDTO updatedResponse = new StudentResponseDTO(1L, "Novo Nome", "novoemail@dominio.com", 8.5, 1L, 1L, StudentInterviewStatus.NAO_ASSOCIADO, false);

//         when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
//         when(studentMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
//         when(studentRepository.save(any(Student.class))).thenReturn(updatedEntity);
//         when(studentMapper.toResponse(updatedEntity)).thenReturn(updatedResponse);

//         StudentResponseDTO result = studentService.update(1L, updatedRequest);

//         assertNotNull(result);
//         assertEquals("Novo Nome", result.name());
//         assertEquals("novoemail@dominio.com", result.email());
//         assertEquals(8.5, result.averageGrade());

//         verify(studentRepository).findById(1L);
//         verify(studentMapper).toEntity(updatedRequest);
//         verify(studentRepository).save(any(Student.class));
//         verify(studentMapper).toResponse(updatedEntity);
//     }

//     @Test
//     @DisplayName("Should delete student")
//     void shouldDeleteStudent() {
//         doNothing().when(studentRepository).deleteById(1L);

//         assertDoesNotThrow(() -> studentService.delete(1L));

//         verify(studentRepository).deleteById(1L);
//     }

//     @Test
//     @DisplayName("Should mark email as read")
//     void shouldMarkEmailAsRead() {
//         StudentResponseDTO readResponse = new StudentResponseDTO(1L, "Nome", "email@dominio.com", 5.0, 1L, 1L, StudentInterviewStatus.NAO_ASSOCIADO, true);

//         when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
//         when(studentRepository.save(any(Student.class))).thenReturn(student);
//         when(studentMapper.toResponse(any(Student.class))).thenReturn(readResponse);

//         StudentResponseDTO result = studentService.markEmailAsRead(1L);

//         assertNotNull(result);
//         assertTrue(result.hasSeenEmail());

//         verify(studentRepository).findById(1L);
//         verify(studentRepository).save(any(Student.class));
//     }
// }