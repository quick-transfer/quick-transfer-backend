package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.admin.AdminFilter;
import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.admin.AdminUpdateRequestDTO;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private static final UUID ADMIN_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID NON_EXISTENT_ADMIN_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174999");

    @BeforeEach
    void setUp() {
        admin = Admin.builder()
                .id(ADMIN_ID)
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
                ADMIN_ID,
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
        assertEquals(ADMIN_ID, result.id());
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
        when(adminRepo.findById(ADMIN_ID)).thenReturn(Optional.of(admin));
        when(adminMapper.toResponse(admin)).thenReturn(responseDTO);

        AdminResponseDTO result = adminService.findAdminById(ADMIN_ID);

        assertNotNull(result);
        assertEquals(ADMIN_ID, result.id());
        assertEquals("Administrador", result.name());

        verify(adminRepo).findById(ADMIN_ID);
        verify(adminMapper).toResponse(admin);
    }

    @Test
    @DisplayName("Should throw exception when admin is not found by id")
    void shouldThrowExceptionWhenAdminNotFound() {
        when(adminRepo.findById(NON_EXISTENT_ADMIN_ID)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> adminService.findAdminById(NON_EXISTENT_ADMIN_ID)
        );

        verify(adminMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should find admin by username or name")
    void shouldFindAdminsByName() {
        when(adminRepo.searchAdminsByName("Admin")).thenReturn(List.of(admin));
        when(adminMapper.toResponse(admin)).thenReturn(responseDTO);

        List<AdminResponseDTO> result = adminService.findAdminByName("Admin");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Administrador", result.get(0).name());

        verify(adminRepo).searchAdminsByName("Admin");
        verify(adminMapper).toResponse(admin);
    }

    @Test
    @DisplayName("Should throw exception when searching admin by blank name")
    void shouldThrowExceptionWhenSearchNameIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> adminService.findAdminByName(" ")
        );

        verify(adminRepo, never()).searchAdminsByName(anyString());
    }

    @Test
    @DisplayName("Should search admins using specification filter")
    @SuppressWarnings("unchecked")
    void shouldSearchAdminsWithFilter() {
        AdminFilter filter = new AdminFilter("Administrador", "admin@weg.com");

        when(adminRepo.findAll(any(Specification.class))).thenReturn(List.of(admin));
        when(adminMapper.toResponse(admin)).thenReturn(responseDTO);

        List<AdminResponseDTO> result = adminService.searchAdmins(filter);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(adminRepo).findAll(any(Specification.class));
        verify(adminMapper).toResponse(admin);
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
        AdminUpdateRequestDTO updateRequestDTO = new AdminUpdateRequestDTO(updatedName, "Supersecure_Password1234");

        AdminResponseDTO updatedResponse = new AdminResponseDTO(
                ADMIN_ID,
                updatedName,
                "admin01",
                "admin@weg.com"
        );

        when(adminRepo.findById(ADMIN_ID)).thenReturn(Optional.of(admin));
        when(adminMapper.toResponse(admin)).thenReturn(updatedResponse);

        AdminResponseDTO result = adminService.updateAdminById(ADMIN_ID, updateRequestDTO);

        assertNotNull(result);
        assertEquals(ADMIN_ID, result.id());
        assertEquals(updatedName, result.name());
        assertEquals(updatedName, admin.getName());

        verify(adminRepo).findById(ADMIN_ID);
        verify(adminMapper).toResponse(admin);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent admin")
    void shouldThrowExceptionWhenUpdatingNonExistentAdmin() {
        AdminUpdateRequestDTO updateRequestDTO = new AdminUpdateRequestDTO("Novo Nome", "Supersecure_Password1234");

        when(adminRepo.findById(NON_EXISTENT_ADMIN_ID)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> adminService.updateAdminById(NON_EXISTENT_ADMIN_ID, updateRequestDTO)
        );

        verify(adminMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should delete admin")
    void shouldDeleteAdmin() {
        when(adminRepo.existsById(ADMIN_ID)).thenReturn(true);
        doNothing().when(adminRepo).deleteById(ADMIN_ID);

        assertDoesNotThrow(() -> adminService.deleteById(ADMIN_ID));

        verify(adminRepo).existsById(ADMIN_ID);
        verify(adminRepo).deleteById(ADMIN_ID);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent admin")
    void shouldThrowExceptionWhenDeletingNonExistentAdmin() {
        when(adminRepo.existsById(NON_EXISTENT_ADMIN_ID)).thenReturn(false);

        assertThrows(
                UserNotFoundException.class,
                () -> adminService.deleteById(NON_EXISTENT_ADMIN_ID)
        );

        verify(adminRepo).existsById(NON_EXISTENT_ADMIN_ID);
        verify(adminRepo, never()).deleteById(any(UUID.class));
    }
}