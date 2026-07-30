package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.manager.ManagerRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
import com.weg.quicktransfer.dto.manager.ManagerUpdateRequestDTO;
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
import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.service.ManagerService;
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
import java.util.UUID;

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
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ManagerService managerService;

    private Manager manager;
    private ManagerRequestDTO requestDTO;
    private ManagerResponseDTO responseDTO;

    private Interview interview;
    private Student student;

    private static final UUID MANAGER_ID      = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID STUDENT_ID      = UUID.fromString("123e4567-e89b-12d3-a456-426614174111");
    private static final UUID INTERVIEW_ID    = UUID.fromString("123e4567-e89b-12d3-a456-426614174222");
    private static final UUID NON_EXISTENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174999");

    @BeforeEach
    void setUp() {
        manager = new Manager();
        manager.setId(MANAGER_ID);
        manager.setName("Manager");
        manager.setUsername("manager01");
        manager.setEmail("manager@dominio.com");
        manager.setPassword("123456");
        manager.setRole(Role.MANAGER);
        manager.setSection(Section.IT);

        requestDTO = new ManagerRequestDTO("Manager", "manager01", "manager@dominio.com", "123456", null);
        responseDTO = new ManagerResponseDTO(MANAGER_ID, "Manager", "manager01", "manager@dominio.com", null);

        // Instâncias auxiliares para os testes de envio de e-mail
        student = new Student();
        student.setId(STUDENT_ID);
        student.setName("Bruno");

        Place place = new Place();
        place.setPlaceName("Auditório Principal");
        place.setPark(Park.WEG_II);
        place.setSection(Section.IT);

        Vacancy vacancy = new Vacancy();
        vacancy.setDescription("Vaga para desenvolvedor Java");

        interview = new Interview();
        interview.setId(INTERVIEW_ID);
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
        assertEquals(MANAGER_ID, result.id());
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
        when(managerRepository.findById(MANAGER_ID)).thenReturn(Optional.of(manager));
        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);

        ManagerResponseDTO result = managerService.findById(MANAGER_ID);

        assertNotNull(result);
        assertEquals(MANAGER_ID, result.id());
        assertEquals("Manager", result.name());

        verify(managerRepository).findById(MANAGER_ID);
        verify(managerMapper).toResponse(manager);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when manager not found by id")
    void shouldThrowExceptionWhenManagerNotFoundById() {
        when(managerRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> managerService.findById(NON_EXISTENT_ID));

        verify(managerRepository).findById(NON_EXISTENT_ID);
    }

    @Test
    @DisplayName("Should find manager by name and return response dto list")
    void shouldFindManagerByName() {
        when(managerRepository.searchUsersByName("Manager")).thenReturn(List.of(manager));
        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);

        List<ManagerResponseDTO> result = managerService.findByName("Manager");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Manager", result.get(0).name());

        verify(managerRepository).searchUsersByName("Manager");
        verify(managerMapper).toResponse(manager);
    }

    @Test
    @DisplayName("Should return empty list when manager not found by name")
    void shouldReturnEmptyListWhenManagerNotFoundByName() {
        when(managerRepository.searchUsersByName("NonExistent")).thenReturn(List.of());

        List<ManagerResponseDTO> result = managerService.findByName("NonExistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(managerRepository).searchUsersByName("NonExistent");
        verifyNoInteractions(managerMapper);
    }

    @Test
    @DisplayName("Should update manager fields and return response dto")
    void shouldUpdateManager() {
        String newName = "Manager Atualizado";
        String newPassword = "StrongP@ssword1!";

        ManagerUpdateRequestDTO updateRequest = new ManagerUpdateRequestDTO(newName, newPassword, Section.IT.toString());
        ManagerResponseDTO updatedResponse = new ManagerResponseDTO(MANAGER_ID, newName, "manager01", "manager@dominio.com", null);

        // manager também é o "User logado" retornado pelo userRepository (Manager é subtipo de User)
        when(userRepository.findById(MANAGER_ID)).thenReturn(Optional.of(manager));
        when(managerRepository.findById(MANAGER_ID)).thenReturn(Optional.of(manager));
        when(managerRepository.save(manager)).thenReturn(manager);
        when(managerMapper.toResponse(manager)).thenReturn(updatedResponse);

        // usa o mesmo MANAGER_ID como id do manager e como userId (referência igual, exigido pelo "==" no service)
        ManagerResponseDTO result = managerService.update(MANAGER_ID, updateRequest, MANAGER_ID);

        assertNotNull(result);
        assertEquals(newName, result.name());
        assertEquals(newPassword, manager.getPassword());

        verify(userRepository).findById(MANAGER_ID);
        verify(managerRepository).findById(MANAGER_ID);
        verify(managerRepository).save(manager);
        verify(managerMapper).toResponse(manager);
    }

    @Test
    @DisplayName("Should not update password if it fails strength regex validation")
    void shouldNotUpdatePasswordWhenInvalidRegex() {
        String newName = "Manager Atualizado";
        String weakPassword = "123456";

        ManagerUpdateRequestDTO updateRequest = new ManagerUpdateRequestDTO(newName, weakPassword, Section.IT.toString());

        when(userRepository.findById(MANAGER_ID)).thenReturn(Optional.of(manager));
        when(managerRepository.findById(MANAGER_ID)).thenReturn(Optional.of(manager));
        when(managerRepository.save(manager)).thenReturn(manager);
        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);

        managerService.update(MANAGER_ID, updateRequest, MANAGER_ID);

        assertEquals("123456", manager.getPassword());
    }

    @Test
    @DisplayName("Should delete manager when id exists")
    void shouldDeleteManager() {
        when(managerRepository.existsById(MANAGER_ID)).thenReturn(true);

        assertDoesNotThrow(() -> managerService.delete(MANAGER_ID));

        verify(managerRepository).existsById(MANAGER_ID);
        verify(managerRepository).deleteById(MANAGER_ID);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent manager")
    void shouldThrowExceptionWhenDeletingNonExistentManager() {
        when(managerRepository.existsById(NON_EXISTENT_ID)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> managerService.delete(NON_EXISTENT_ID));

        verify(managerRepository).existsById(NON_EXISTENT_ID);
        verify(managerRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Should send dynamic AMP email successfully")
    void shouldSendDynamicAmpEmail() {
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(interview));
        when(studentRepository.findByInterviewId(INTERVIEW_ID)).thenReturn(Optional.of(student));
        when(managerRepository.findByInterviewId(INTERVIEW_ID)).thenReturn(Optional.of(manager));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() -> managerService.sendDynamicEmailAmp("destinatario@dominio.com", INTERVIEW_ID));

        verify(interviewRepository).findById(INTERVIEW_ID);
        verify(studentRepository).findByInterviewId(INTERVIEW_ID);
        verify(managerRepository).findByInterviewId(INTERVIEW_ID);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Should throw InvalidEmailException when email format is invalid")
    void shouldThrowInvalidEmailExceptionWhenEmailIsInvalid() {
        assertThrows(InvalidEmailException.class, () -> managerService.sendDynamicEmailAmp("email-invalido", INTERVIEW_ID));

        verifyNoInteractions(interviewRepository, studentRepository, managerRepository, mailSender);
    }

    @Test
    @DisplayName("Should throw InterviewNotFoundException when interview does not exist on email send")
    void shouldThrowInterviewNotFoundExceptionWhenInterviewNotFound() {
        when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.empty());

        assertThrows(InterviewNotFoundException.class, () -> managerService.sendDynamicEmailAmp("destinatario@dominio.com", INTERVIEW_ID));

        verify(interviewRepository).findById(INTERVIEW_ID);
        verifyNoInteractions(studentRepository, mailSender);
    }

    @Test
    @DisplayName("Should throw Exception when mail sender fails to send email")
    void shouldThrowExceptionWhenMailSendingFails() {
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(interview));
        when(studentRepository.findByInterviewId(INTERVIEW_ID)).thenReturn(Optional.of(student));
        when(managerRepository.findByInterviewId(INTERVIEW_ID)).thenReturn(Optional.of(manager));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server offline")).when(mailSender).send(any(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> managerService.sendDynamicEmailAmp("destinatario@dominio.com", INTERVIEW_ID));
    }
}