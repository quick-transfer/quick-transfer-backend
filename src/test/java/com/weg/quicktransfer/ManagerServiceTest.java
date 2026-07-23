//package com.weg.quicktransfer;
//
//import com.weg.quicktransfer.dto.manager.ManagerRequestDTO;
//import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
//import com.weg.quicktransfer.enums.Role;
//import com.weg.quicktransfer.exception.UserNotFoundException;
//import com.weg.quicktransfer.mapper.ManagerMapper;
//import com.weg.quicktransfer.model.Manager;
//import com.weg.quicktransfer.repo.ManagerRepository;
//import com.weg.quicktransfer.service.ManagerService;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mail.javamail.JavaMailSender;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ManagerServiceTest {
//
//    @Mock
//    private ManagerRepository managerRepository;
//
//    @Mock
//    private ManagerMapper managerMapper;
//
//    @Mock
//    private JavaMailSender mailSender;
//
//    @InjectMocks
//    private ManagerService managerService;
//
//    private Manager manager;
//    private ManagerRequestDTO requestDTO;
//    private ManagerResponseDTO responseDTO;
//
//    @BeforeEach
//    void setUp() {
//        manager = new Manager();
//        manager.setId(1L);
//        manager.setName("Manager");
//        manager.setUsername("manager01");
//        manager.setEmail("manager@dominio.com");
//        manager.setPassword("123456");
//        manager.setRole(Role.MANAGER);
//        manager.setSection(null);
//
//        requestDTO = new ManagerRequestDTO("Manager", "manager01", "manager@dominio.com", "123456", null);
//        responseDTO = new ManagerResponseDTO(1L, "Manager", "manager01", "manager@dominio.com", null);
//    }
//
//    @Test
//    @DisplayName("Should create manager and return response dto")
//    void shouldCreateManager() {
//        when(managerMapper.toEntity(requestDTO)).thenReturn(manager);
//        when(managerRepository.save(manager)).thenReturn(manager);
//        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);
//
//        ManagerResponseDTO result = managerService.create(requestDTO);
//
//        assertNotNull(result);
//        assertEquals(1L, result.id());
//        assertEquals("Manager", result.name());
//
//        verify(managerMapper).toEntity(requestDTO);
//        verify(managerRepository).save(manager);
//        verify(managerMapper).toResponse(manager);
//    }
//
//    @Test
//    @DisplayName("Should throw exception when request dto is null")
//    void shouldThrowExceptionWhenRequestDtoIsNull() {
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> managerService.create(null)
//        );
//
//        assertEquals("Manager can not be null", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Should return list of all managers")
//    void shouldFindAllManagers() {
//        when(managerRepository.findAll()).thenReturn(List.of(manager));
//        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);
//
//        List<ManagerResponseDTO> result = managerService.findAll();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("Manager", result.get(0).name());
//
//        verify(managerRepository).findAll();
//        verify(managerMapper).toResponse(manager);
//    }
//
//    @Test
//    @DisplayName("Should find manager by id and return response dto")
//    void shouldFindManagerById() {
//        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));
//        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);
//
//        ManagerResponseDTO result = managerService.findById(1L);
//
//        assertNotNull(result);
//        assertEquals(1L, result.id());
//        assertEquals("Manager", result.name());
//
//        verify(managerRepository).findById(1L);
//        verify(managerMapper).toResponse(manager);
//    }
//
//    @Test
//    @DisplayName("Should throw UserNotFoundException when manager not found by id")
//    void shouldThrowExceptionWhenManagerNotFoundById() {
//        when(managerRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(UserNotFoundException.class, () -> managerService.findById(1L));
//
//        verify(managerRepository).findById(1L);
//    }
//
//    @Test
//    @DisplayName("Should find manager by name and return response dto")
//    void shouldFindManagerByName() {
//        when(managerRepository.findByName("Manager")).thenReturn(Optional.of(manager));
//        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);
//
//        ManagerResponseDTO result = managerService.findByName("Manager");
//
//        assertNotNull(result);
//        assertEquals("Manager", result.name());
//
//        verify(managerRepository).findByName("Manager");
//        verify(managerMapper).toResponse(manager);
//    }
//
//    @Test
//    @DisplayName("Should throw UserNotFoundException when manager not found by name")
//    void shouldThrowExceptionWhenManagerNotFoundByName() {
//        when(managerRepository.findByName("NonExistent")).thenReturn(Optional.empty());
//
//        assertThrows(UserNotFoundException.class, () -> managerService.findByName("NonExistent"));
//
//        verify(managerRepository).findByName("NonExistent");
//    }
//
//    @Test
//    @DisplayName("Should update manager fields and return response dto")
//    void shouldUpdateManager() {
//        String newName = "Manager Atualizado";
//        String newEmail = "manager2@dominio.com";
//        // Meets regex: 14+ chars, 1 uppercase, 1 digit, 1 special character
//        String newPassword = "StrongP@ssword1";
//
//        ManagerResponseDTO updatedResponse = new ManagerResponseDTO(1L, newName, "manager01", newEmail, null);
//
//        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));
//        when(managerRepository.save(manager)).thenReturn(manager);
//        when(managerMapper.toResponse(manager)).thenReturn(updatedResponse);
//
//        ManagerResponseDTO result = managerService.update(1L, newName, newEmail, newPassword);
//
//        assertNotNull(result);
//        assertEquals(newName, result.name());
//        assertEquals(newEmail, result.email());
//        assertEquals(newPassword, manager.getPassword());
//
//        verify(managerRepository).findById(1L);
//        verify(managerRepository).save(manager);
//        verify(managerMapper).toResponse(manager);
//    }
//
//    @Test
//    @DisplayName("Should not update password if it fails strength regex validation")
//    void shouldNotUpdatePasswordWhenInvalidRegex() {
//        String newName = "Manager Atualizado";
//        String newEmail = "manager2@dominio.com";
//        String weakPassword = "123456"; // Fails regex criteria
//
//        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));
//        when(managerRepository.save(manager)).thenReturn(manager);
//
//        managerService.update(1L, newName, newEmail, weakPassword);
//
//        // Password should remain unchanged
//        assertEquals("123456", manager.getPassword());
//    }
//
//    @Test
//    @DisplayName("Should delete manager when id exists")
//    void shouldDeleteManager() {
//        when(managerRepository.existsById(1L)).thenReturn(true);
//
//        assertDoesNotThrow(() -> managerService.delete(1L));
//
//        verify(managerRepository).existsById(1L);
//        verify(managerRepository).deleteById(1L);
//    }
//
//    @Test
//    @DisplayName("Should throw UserNotFoundException when deleting non-existent manager")
//    void shouldThrowExceptionWhenDeletingNonExistentManager() {
//        when(managerRepository.existsById(1L)).thenReturn(false);
//
//        assertThrows(UserNotFoundException.class, () -> managerService.delete(1L));
//
//        verify(managerRepository).existsById(1L);
//        verify(managerRepository, never()).deleteById(anyLong());
//    }
//
//    @Test
//    @DisplayName("Should send dynamic AMP email successfully")
//    void shouldSendDynamicAmpEmail() {
//        MimeMessage mimeMessage = mock(MimeMessage.class);
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//        assertDoesNotThrow(() -> managerService.enviarEmailDinamicoAmp("destinatario@dominio.com", 1L));
//
//        verify(mailSender).createMimeMessage();
//        verify(mailSender).send(mimeMessage);
//    }
//
//    @Test
//    @DisplayName("Should throw RuntimeException when email creation or sending fails")
//    void shouldThrowRuntimeExceptionWhenMailFails() {
//        when(mailSender.createMimeMessage()).thenThrow(new MessagingException("Mail server error"));
//
//        assertThrows(MessagingException.class, () -> managerService.enviarEmailDinamicoAmp("destinatario@dominio.com", 1L));
//    }
//}