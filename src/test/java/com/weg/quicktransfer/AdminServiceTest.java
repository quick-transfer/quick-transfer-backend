package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.AdminRequestDTO;
import com.weg.quicktransfer.dto.AdminResponseDTO;
import com.weg.quicktransfer.mapper.AdminMapper;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.model.Role;
import com.weg.quicktransfer.repo.AdminRepo;
import com.weg.quicktransfer.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepo adminRepo;

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private AdminService adminService;

    private Admin admin;
    private AdminRequestDTO requestDTO;
    private AdminResponseDTO responseDTO;

    @BeforeEach
    void setUp() {

        admin = new Admin();
        admin.setId(1L);
        admin.setName("Administrador");
        admin.setUsername("admin01");
        admin.setEmail("admin@weg.com");
        admin.setPassword("123456");
        admin.setRole(Role.ADMIN);

        requestDTO = new AdminRequestDTO();
        requestDTO.setName("Administrador");
        requestDTO.setUsername("admin01");
        requestDTO.setEmail("admin@weg.com");
        requestDTO.setPassword("123456");

        responseDTO = new AdminResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Administrador");
        responseDTO.setUsername("admin01");
        responseDTO.setEmail("admin@weg.com");
        responseDTO.setRole(Role.ADMIN);
    }

    @Test
    @DisplayName("Should create admin and return response dto")
    void shouldCreateAdmin() {

        when(adminMapper.toEntity(requestDTO))
                .thenReturn(admin);

        when(adminRepo.save(admin))
                .thenReturn(admin);

        when(adminMapper.toResponseDTO(admin))
                .thenReturn(responseDTO);

        AdminResponseDTO result =
                adminService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Administrador", result.getName());
        assertEquals("admin01", result.getUsername());
        assertEquals("admin@weg.com", result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());

        verify(adminMapper).toEntity(requestDTO);
        verify(adminRepo).save(admin);
        verify(adminMapper).toResponseDTO(admin);
    }

    @Test
    @DisplayName("Should find admin by id and return response dto")
    void shouldFindAdminById() {

        when(adminRepo.findById(1L))
                .thenReturn(admin);

        when(adminMapper.toResponseDTO(admin))
                .thenReturn(responseDTO);

        AdminResponseDTO result =
                adminService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Administrador", result.getName());

        verify(adminRepo).findById(1L);
        verify(adminMapper).toResponseDTO(admin);
    }

    @Test
    @DisplayName("Should update admin and return response dto")
    void shouldUpdateAdmin() {

        AdminRequestDTO updatedRequest =
                new AdminRequestDTO();

        updatedRequest.setName("Administrador Atualizado");
        updatedRequest.setUsername("admin02");
        updatedRequest.setEmail("admin2@weg.com");
        updatedRequest.setPassword("654321");

        Admin updatedEntity = new Admin();
        updatedEntity.setId(1L);
        updatedEntity.setName("Administrador Atualizado");
        updatedEntity.setUsername("admin02");
        updatedEntity.setEmail("admin2@weg.com");
        updatedEntity.setPassword("654321");
        updatedEntity.setRole(Role.ADMIN);

        AdminResponseDTO updatedResponse =
                new AdminResponseDTO();

        updatedResponse.setId(1L);
        updatedResponse.setName("Administrador Atualizado");
        updatedResponse.setUsername("admin02");
        updatedResponse.setEmail("admin2@weg.com");
        updatedResponse.setRole(Role.ADMIN);

        when(adminRepo.findById(1L))
                .thenReturn(admin);

        when(adminMapper.toEntity(updatedRequest))
                .thenReturn(updatedEntity);

        when(adminRepo.save(any(Admin.class)))
                .thenReturn(updatedEntity);

        when(adminMapper.toResponseDTO(updatedEntity))
                .thenReturn(updatedResponse);

        AdminResponseDTO result =
                adminService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals("Administrador Atualizado", result.getName());
        assertEquals("admin02", result.getUsername());
        assertEquals("admin2@weg.com", result.getEmail());

        verify(adminRepo).findById(1L);
        verify(adminMapper).toEntity(updatedRequest);
        verify(adminRepo).save(any(Admin.class));
        verify(adminMapper).toResponseDTO(updatedEntity);
    }

    @Test
    @DisplayName("Should delete admin")
    void shouldDeleteAdmin() {

        doNothing()
                .when(adminRepo)
                .deleteById(1L);

        assertDoesNotThrow(() ->
                adminService.delete(1L));

        verify(adminRepo)
                .deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminService.create(null)
        );
    }
}