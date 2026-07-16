package com.weg.quicktransfer;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @MockitoBean
    private StudentRepo studentRepo;

    private Student validStudent;

    @BeforeEach
    void setUp() {
        Coordinator coordinator = new Coordinator();
        Course course = new Course("Nome", coordinator, new ArrayList<ClassEntity>());
        ClassEntity classEntity = new ClassEntity(course, LocalDate.now(), new ArrayList<Student>(), "SIGLA01");

        validStudent = new Student(
                "Nome",
                "email@dominio.com",
                5.0,
                classEntity,
                classEntity,
                StudentInterviewStatus.NOT_ASSIGNED,
                false
        );

        when(studentRepo.createStudent(any(Student.class))).thenAnswer(invocation -> {
            Student student = invocation.getArgument(0);
            if (student.getId() == null) {
                student.setId(1L);
            }
            return student;
        });

        when(studentRepo.create(any(Student.class))).thenAnswer(invocation -> {
            Student student = invocation.getArgument(0);
            if (student.getId() == null) {
                student.setId(1L);
            }
            return student;
        });

        when(studentRepo.findById(any())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            Student student = new Student(
                    "Nome",
                    "email@dominio.com",
                    5.0,
                    null,
                    null,
                    StudentInterviewStatus.NOT_ASSIGNED,
                    false
            );
            student.setId(id);
            return student;
        });
    }

    @Test
    @DisplayName("Should create a student")
    public void createStudent() {
        Student created = studentRepo.createStudent(validStudent);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(validStudent.getName(), created.getName());
        assertEquals(validStudent.getEmail(), created.getEmail());
        assertEquals(validStudent.getScore(), created.getScore());
    }

    @Test
    @DisplayName("Should throw exception if any atribute is null")
    public void shouldThrowExceptionIfAnyAttributeIsNull() {
        Student studentWithNullName = new Student(
                null,
                "email@dominio.com",
                5.0,
                null,
                null,
                StudentInterviewStatus.NOT_ASSIGNED,
                false
        );

        assertThrows(RuntimeException.class, () -> studentService.createStudent(studentWithNullName));
    }

    @Test
    @DisplayName("Should find student by id")
    public void findById() {
        validStudent.setId(1L);

        Student found = studentRepo.findById(validStudent.getId());

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