// ManagerServiceTest.java
package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.ManagerRequestDTO;
import com.weg.quicktransfer.dto.ManagerResponseDTO;
import com.weg.quicktransfer.mapper.ManagerMapper;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.Role;
import com.weg.quicktransfer.model.Section;
import com.weg.quicktransfer.repo.ManagerRepo;
import com.weg.quicktransfer.service.ManagerService;
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
class ManagerServiceTest {

    @Mock
    private ManagerRepo managerRepo;

    @Mock
    private ManagerMapper managerMapper;

    @InjectMocks
    private ManagerService managerService;

    private Manager manager;
    private ManagerRequestDTO requestDTO;
    private ManagerResponseDTO responseDTO;

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

        requestDTO = new ManagerRequestDTO();
        requestDTO.setName("Manager");
        requestDTO.setUsername("manager01");
        requestDTO.setEmail("manager@dominio.com");
        requestDTO.setPassword("123456");
        requestDTO.setSection(null);

        responseDTO = new ManagerResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Manager");
        responseDTO.setUsername("manager01");
        responseDTO.setEmail("manager@dominio.com");
        responseDTO.setRole(Role.MANAGER);
        responseDTO.setSection(null);
    }

    @Test
    @DisplayName("Should create manager and return response dto")
    void shouldCreateManager() {
        when(managerMapper.toEntity(requestDTO)).thenReturn(manager);
        when(managerRepo.save(manager)).thenReturn(manager);
        when(managerMapper.toResponseDTO(manager)).thenReturn(responseDTO);

        ManagerResponseDTO result = managerService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Manager", result.getName());
        assertEquals("manager01", result.getUsername());
        assertEquals(Role.MANAGER, result.getRole());

        verify(managerMapper).toEntity(requestDTO);
        verify(managerRepo).save(manager);
        verify(managerMapper).toResponseDTO(manager);
    }

    @Test
    @DisplayName("Should find manager by id and return response dto")
    void shouldFindManagerById() {
        when(managerRepo.findById(1L)).thenReturn(manager);
        when(managerMapper.toResponseDTO(manager)).thenReturn(responseDTO);

        ManagerResponseDTO result = managerService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Manager", result.getName());

        verify(managerRepo).findById(1L);
        verify(managerMapper).toResponseDTO(manager);
    }

    @Test
    @DisplayName("Should update manager and return response dto")
    void shouldUpdateManager() {
        ManagerRequestDTO updatedRequest = new ManagerRequestDTO();
        updatedRequest.setName("Manager Atualizado");
        updatedRequest.setUsername("manager02");
        updatedRequest.setEmail("manager2@dominio.com");
        updatedRequest.setPassword("123456");
        updatedRequest.setSection(null);

        Manager updatedEntity = new Manager();
        updatedEntity.setId(1L);
        updatedEntity.setName("Manager Atualizado");
        updatedEntity.setUsername("manager02");
        updatedEntity.setEmail("manager2@dominio.com");
        updatedEntity.setPassword("123456");
        updatedEntity.setRole(Role.MANAGER);
        updatedEntity.setSection(null);

        ManagerResponseDTO updatedResponse = new ManagerResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Manager Atualizado");
        updatedResponse.setUsername("manager02");
        updatedResponse.setEmail("manager2@dominio.com");
        updatedResponse.setRole(Role.MANAGER);
        updatedResponse.setSection(null);

        when(managerRepo.findById(1L)).thenReturn(manager);
        when(managerMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
        when(managerRepo.save(any(Manager.class))).thenReturn(updatedEntity);
        when(managerMapper.toResponseDTO(updatedEntity)).thenReturn(updatedResponse);

        ManagerResponseDTO result = managerService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals("Manager Atualizado", result.getName());
        assertEquals("manager02", result.getUsername());
        assertEquals(Role.MANAGER, result.getRole());

        verify(managerRepo).findById(1L);
        verify(managerMapper).toEntity(updatedRequest);
        verify(managerRepo).save(any(Manager.class));
        verify(managerMapper).toResponseDTO(updatedEntity);
    }

    @Test
    @DisplayName("Should delete manager")
    void shouldDeleteManager() {
        doNothing().when(managerRepo).deleteById(1L);

        assertDoesNotThrow(() -> managerService.delete(1L));

        verify(managerRepo).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> managerService.create(null));
    }
}