package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.manager.ManagerRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
import com.weg.quicktransfer.mapper.ManagerMapper;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.repo.ManagerRepository;
import com.weg.quicktransfer.service.ManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;

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

        // Inicialização utilizando os construtores canônicos dos Records
        requestDTO = new ManagerRequestDTO("Manager", "manager01", "manager@dominio.com", "123456", null);
        responseDTO = new ManagerResponseDTO(1L, "Manager", "manager01", "manager@dominio.com", Role.MANAGER, null);
    }

    @Test
    @DisplayName("Should create manager and return response dto")
    void shouldCreateManager() {
        when(managerMapper.toEntity(requestDTO)).thenReturn(manager);
        when(managerRepository.save(manager)).thenReturn(manager);
        when(managerMapper.toResponse(manager)).thenReturn(responseDTO);

        ManagerResponseDTO result = managerService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id()); // Acesso direto ao componente do record
        assertEquals("Manager", result.name());
        assertEquals("manager01", result.username());
        assertEquals(Role.MANAGER, result.role());

        verify(managerMapper).toEntity(requestDTO);
        verify(managerRepository).save(manager);
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
    @DisplayName("Should update manager and return response dto")
    void shouldUpdateManager() {
        // Records são imutáveis; novas instâncias representam as modificações
        ManagerRequestDTO updatedRequest = new ManagerRequestDTO("Manager Atualizado", "manager02", "manager2@dominio.com", "123456", null);

        Manager updatedEntity = new Manager();
        updatedEntity.setId(1L);
        updatedEntity.setName("Manager Atualizado");
        updatedEntity.setUsername("manager02");
        updatedEntity.setEmail("manager2@dominio.com");
        updatedEntity.setPassword("123456");
        updatedEntity.setRole(Role.MANAGER);
        updatedEntity.setSection(null);

        ManagerResponseDTO updatedResponse = new ManagerResponseDTO(1L, "Manager Atualizado", "manager02", "manager2@dominio.com", Role.MANAGER, null);

        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(managerMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
        when(managerRepository.save(any(Manager.class))).thenReturn(updatedEntity);
        when(managerMapper.toResponse(updatedEntity)).thenReturn(updatedResponse);

        ManagerResponseDTO result = managerService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals("Manager Atualizado", result.name());
        assertEquals("manager02", result.username());
        assertEquals(Role.MANAGER, result.role());

        verify(managerRepository).findById(1L);
        verify(managerMapper).toEntity(updatedRequest);
        verify(managerRepository).save(any(Manager.class));
        verify(managerMapper).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Should delete manager")
    void shouldDeleteManager() {
        doNothing().when(managerRepository).deleteById(1L);

        assertDoesNotThrow(() -> managerService.delete(1L));

        verify(managerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> managerService.create(null));
    }
}