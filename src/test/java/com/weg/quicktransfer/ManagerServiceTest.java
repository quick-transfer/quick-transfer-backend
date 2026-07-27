package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.manager.ManagerRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.exception.InterviewNotFoundException;
import com.weg.quicktransfer.exception.InvalidEmailException;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.ManagerMapper;
import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.repo.ManagerRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import com.weg.quicktransfer.service.ManagerService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private ManagerMapper managerMapper;

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ManagerService managerService;

    private Manager manager;
    private ManagerRequestDTO requestDTO;
    private ManagerResponseDTO responseDTO;

    private Interview interview;
    private Student student;

    @BeforeEach
    void setUp() {
        manager = new Manager();
        manager.setId(1L);
        manager.setName("Manager");
        manager.setUsername("manager01");
        manager.setEmail("manager@dominio.com");
        manager.setPassword("123456");
        manager.setRole(Role.MANAGER);
        manager.setSection(null);

        requestDTO = new ManagerRequestDTO("Manager", "manager01", "manager@dominio.com", "123456", null);
        responseDTO = new ManagerResponseDTO(1L, "Manager", "manager01", "manager@dominio.com", null);

        // Instâncias auxiliares para os testes de envio de e-mail
        student = new Student();
        student.setId(10L);
        student.setName("Bruno");

        Place place = new Place();
        place.setPlaceName("Auditório Principal");
        place.setPark(Park.WEG_II);
        place.setSection(Section.TI);

        Vacancy vacancy = new Vacancy();
        vacancy.setDescription("Vaga para desenvolvedor Java");

        interview = new Interview();
        interview.setId(1L);
        interview.setDateTime(LocalDateTime.of(2026, 8, 1, 14, 0));
        interview.setPlace(place);
        interview.setVacancy(vacancy);
        interview.setInterviewerName("Lucas");
    }

    @Test
    @DisplayName("Should create manager and return response dto")
    void shouldCreateManager() {
        when(managerMapper.toEntity(requestDTO)).thenReturn(manager);
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        when(managerRepository.save(manager)).thenReturn(manager);
        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);

        ManagerResponseDTO result = managerService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Manager", result.name());

        verify(managerMapper).toEntity(requestDTO);
        verify(passwordEncoder).encode("123456");
        verify(managerRepository).save(manager);
        verify(managerMapper).toResponse(manager);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> managerService.create(null)
        );

        assertEquals("Manager can not be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should return list of all managers")
    void shouldFindAllManagers() {
        when(managerRepository.findAll()).thenReturn(List.of(manager));
        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);

        List<ManagerResponseDTO> result = managerService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Manager", result.get(0).name());

        verify(managerRepository).findAll();
        verify(managerMapper).toResponse(manager);
    }

    @Test
    @DisplayName("Should find manager by id and return response dto")
    void shouldFindManagerById() {
        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);

        ManagerResponseDTO result = managerService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Manager", result.name());

        verify(managerRepository).findById(1L);
        verify(managerMapper).toResponse(manager);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when manager not found by id")
    void shouldThrowExceptionWhenManagerNotFoundById() {
        when(managerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> managerService.findById(1L));

        verify(managerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find manager by name and return response dto")
    void shouldFindManagerByName() {
        when(managerRepository.findByName("Manager")).thenReturn(Optional.of(manager));
        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);

        ManagerResponseDTO result = managerService.findByName("Manager");

        assertNotNull(result);
        assertEquals("Manager", result.name());

        verify(managerRepository).findByName("Manager");
        verify(managerMapper).toResponse(manager);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when manager not found by name")
    void shouldThrowExceptionWhenManagerNotFoundByName() {
        when(managerRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> managerService.findByName("NonExistent"));

        verify(managerRepository).findByName("NonExistent");
    }

    @Test
    @DisplayName("Should update manager fields and return response dto")
    void shouldUpdateManager() {
        String newName = "Manager Atualizado";
        String newEmail = "manager2@dominio.com";
        String newPassword = "StrongP@ssword1";

        ManagerResponseDTO updatedResponse = new ManagerResponseDTO(1L, newName, "manager01", newEmail, null);

        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(managerRepository.save(manager)).thenReturn(manager);
        when(managerMapper.toResponse(manager)).thenReturn(updatedResponse);

        ManagerResponseDTO result = managerService.update(1L, newName, newEmail, newPassword);

        assertNotNull(result);
        assertEquals(newName, result.name());
        assertEquals(newEmail, result.email());
        assertEquals(newPassword, manager.getPassword());

        verify(managerRepository).findById(1L);
        verify(managerRepository).save(manager);
        verify(managerMapper).toResponse(manager);
    }

    @Test
    @DisplayName("Should not update password if it fails strength regex validation")
    void shouldNotUpdatePasswordWhenInvalidRegex() {
        String newName = "Manager Atualizado";
        String newEmail = "manager2@dominio.com";
        String weakPassword = "123456";

        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(managerRepository.save(manager)).thenReturn(manager);

        managerService.update(1L, newName, newEmail, weakPassword);

        assertEquals("123456", manager.getPassword());
    }

    @Test
    @DisplayName("Should delete manager when id exists")
    void shouldDeleteManager() {
        when(managerRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> managerService.delete(1L));

        verify(managerRepository).existsById(1L);
        verify(managerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent manager")
    void shouldThrowExceptionWhenDeletingNonExistentManager() {
        when(managerRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> managerService.delete(1L));

        verify(managerRepository).existsById(1L);
        verify(managerRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should send dynamic AMP email successfully")
    void shouldSendDynamicAmpEmail() {
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(studentRepository.findByInterviewId(1L)).thenReturn(Optional.of(student));
        when(managerRepository.findByInterviewId(1L)).thenReturn(Optional.of(manager.getName()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() -> managerService.enviarEmailDinamicoAmp("destinatario@dominio.com", 1L));

        verify(interviewRepository).findById(1L);
        verify(studentRepository).findByInterviewId(1L);
        verify(managerRepository).findByInterviewId(1L);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Should throw InvalidEmailException when email format is invalid")
    void shouldThrowInvalidEmailExceptionWhenEmailIsInvalid() {
        assertThrows(InvalidEmailException.class, () -> managerService.enviarEmailDinamicoAmp("email-invalido", 1L));

        verifyNoInteractions(interviewRepository, studentRepository, managerRepository, mailSender);
    }

    @Test
    @DisplayName("Should throw InterviewNotFoundException when interview does not exist on email send")
    void shouldThrowInterviewNotFoundExceptionWhenInterviewNotFound() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InterviewNotFoundException.class, () -> managerService.enviarEmailDinamicoAmp("destinatario@dominio.com", 1L));

        verify(interviewRepository).findById(1L);
        verifyNoInteractions(studentRepository, mailSender);
    }

    @Test
    @DisplayName("Should throw Exception when mail sender fails to send email")
    void shouldThrowExceptionWhenMailSendingFails() {
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(studentRepository.findByInterviewId(1L)).thenReturn(Optional.of(student));
        when(managerRepository.findByInterviewId(1L)).thenReturn(Optional.of(manager.getName()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server offline")).when(mailSender).send(any(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> managerService.enviarEmailDinamicoAmp("destinatario@dominio.com", 1L));
    }
}