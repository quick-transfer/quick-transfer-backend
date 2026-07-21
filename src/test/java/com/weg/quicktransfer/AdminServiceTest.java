package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.AdminMapper;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.repo.AdminRepository;
import com.weg.quicktransfer.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepo;

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    private Admin admin;
    private AdminRequestDTO requestDTO;
    private AdminResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        admin = Admin.builder()
                .id(1L)
                .name("Administrador")
                .username("admin01")
                .email("admin@weg.com")
                .password("123456")
                .role(Role.ADMIN)
                .build();

        requestDTO = new AdminRequestDTO(
                "Administrador",
                "admin01",
                "admin@weg.com",
                "123456"
        );

        responseDTO = new AdminResponseDTO(
                1L,
                "Administrador",
                "admin01",
                "admin@weg.com"
        );
    }

    @Test
    @DisplayName("Should create admin and return response dto")
    void shouldCreateAdmin() {
        when(adminMapper.toEntity(requestDTO)).thenReturn(admin);
        when(passwordEncoder.encode("123456")).thenReturn("encoded_123456");
        when(adminRepo.save(admin)).thenReturn(admin);
        when(adminMapper.toResponse(admin)).thenReturn(responseDTO);

        AdminResponseDTO result = adminService.saveAdmin(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Administrador", result.name());
        assertEquals("admin01", result.username());
        assertEquals("admin@weg.com", result.email());

        verify(adminMapper).toEntity(requestDTO);
        verify(passwordEncoder).encode("123456");
        verify(adminRepo).save(admin);
        verify(adminMapper).toResponse(admin);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> adminService.saveAdmin(null)
        );

        verify(adminRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should find admin by id and return response dto")
    void shouldFindAdminById() {
        when(adminRepo.findById(1L)).thenReturn(Optional.of(admin));
        when(adminMapper.toResponse(admin)).thenReturn(responseDTO);

        AdminResponseDTO result = adminService.findAdminById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Administrador", result.name());

        verify(adminRepo).findById(1L);
        verify(adminMapper).toResponse(admin);
    }

    @Test
    @DisplayName("Should throw exception when admin is not found by id")
    void shouldThrowExceptionWhenAdminNotFound() {
        when(adminRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> adminService.findAdminById(1L)
        );

        verify(adminMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should throw exception when finding admin with invalid id")
    void shouldThrowExceptionWhenFindingAdminWithInvalidId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> adminService.findAdminById(0L)
        );

        verify(adminRepo, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should find admins by name")
    void shouldFindAdminsByName() {
        when(adminRepo.findByNameContaining("Admin")).thenReturn(List.of(admin));
        when(adminMapper.toResponse(admin)).thenReturn(responseDTO);

        List<AdminResponseDTO> result = adminService.findAdminByName("Admin");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(adminRepo).findByNameContaining("Admin");
    }

    @Test
    @DisplayName("Should throw exception when searching admin by blank name")
    void shouldThrowExceptionWhenSearchNameIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> adminService.findAdminByName("   ")
        );

        verify(adminRepo, never()).findByNameContaining(anyString());
    }

    @Test
    @DisplayName("Should return all admins")
    void shouldFindAllAdmins() {
        when(adminRepo.findAll()).thenReturn(List.of(admin));
        when(adminMapper.toResponse(admin)).thenReturn(responseDTO);

        List<AdminResponseDTO> result = adminService.findAllAdmin();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(adminRepo).findAll();
    }

    @Test
    @DisplayName("Should update admin and return response dto")
    void shouldUpdateAdmin() {
        String updatedName = "Administrador Atualizado";
        String updatedEmail = "admin2@weg.com";

        AdminResponseDTO updatedResponse = new AdminResponseDTO(
                1L,
                updatedName,
                "admin01",
                updatedEmail
        );

        when(adminRepo.findById(1L)).thenReturn(Optional.of(admin));
        when(adminMapper.toResponse(admin)).thenReturn(updatedResponse);

        AdminResponseDTO result = adminService.updateAdminById(1L, updatedName, updatedEmail);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(updatedName, result.name());
        assertEquals(updatedEmail, result.email());

        assertEquals(updatedName, admin.getName());
        assertEquals(updatedEmail, admin.getEmail());

        verify(adminRepo).findById(1L);
        verify(adminMapper).toResponse(admin);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent admin")
    void shouldThrowExceptionWhenUpdatingNonExistentAdmin() {
        when(adminRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> adminService.updateAdminById(1L, "Novo Nome", "novo@email.com")
        );

        verify(adminMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid id")
    void shouldThrowExceptionWhenUpdatingWithInvalidId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> adminService.updateAdminById(-1L, "Novo Nome", "novo@email.com")
        );

        verify(adminRepo, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should delete admin")
    void shouldDeleteAdmin() {
        when(adminRepo.existsById(1L)).thenReturn(true);
        doNothing().when(adminRepo).deleteById(1L);

        assertDoesNotThrow(() -> adminService.deleteById(1L));

        verify(adminRepo).existsById(1L);
        verify(adminRepo).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent admin")
    void shouldThrowExceptionWhenDeletingNonExistentAdmin() {
        when(adminRepo.existsById(999L)).thenReturn(false);

        assertThrows(
                UserNotFoundException.class,
                () -> adminService.deleteById(999L)
        );

        verify(adminRepo).existsById(999L);
        verify(adminRepo, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when deleting with invalid id")
    void shouldThrowExceptionWhenDeletingWithInvalidId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> adminService.deleteById(0L)
        );

        verify(adminRepo, never()).existsById(anyLong());
    }
}
