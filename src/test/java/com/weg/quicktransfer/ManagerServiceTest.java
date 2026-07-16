package com.weg.quicktransfer;

import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.Role;
import com.weg.quicktransfer.model.Section;
import com.weg.quicktransfer.repo.ManagerRepo;
import com.weg.quicktransfer.service.ManagerService;
import com.weg.quicktransfer.dto.ManagerRequestDTO;
import com.weg.quicktransfer.dto.ManagerResponseDTO;
import com.weg.quicktransfer.mapper.ManagerMapper;
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

        requestDTO = new ManagerRequestDTO(
                "João",
                "joao@weg.net",
                "123456",
                Section.MANUFACTURING
        );

        manager = new Manager();
        manager.setId(1L);
        manager.setName("João");
        manager.setEmail("joao@weg.net");

        responseDTO = new ManagerResponseDTO(
                1L,
                "João",
                "joao@weg.net",
                Section.MANUFACTURING
        );
    }\

    @Test
    @DisplayName("Should create manager")
    void shouldCreateManager() {

        when(managerMapper.toEntity(requestDTO))
                .thenReturn(manager);

        when(managerRepo.save(manager))
                .thenReturn(manager);

        when(managerMapper.toResponseDTO(manager))
                .thenReturn(responseDTO);

        ManagerResponseDTO result =
                managerService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João", result.getName());

        verify(managerMapper).toEntity(requestDTO);
        verify(managerRepo).save(manager);
        verify(managerMapper).toResponseDTO(manager);
    }

    @Test
    @DisplayName("Should find manager by id")
    void shouldFindManagerById() {

        when(managerRepo.findById(1L))
                .thenReturn(manager);

        when(managerMapper.toResponseDTO(manager))
                .thenReturn(responseDTO);

        ManagerResponseDTO result =
                managerService.findById(1L);

        assertEquals(1L, result.getId());

        verify(managerRepo).findById(1L);
        verify(managerMapper).toResponseDTO(manager);
    }

    @Test
    @DisplayName("Should update manager")
    void shouldUpdateManager() {

        when(managerRepo.findById(1L))
                .thenReturn(manager);

        when(managerMapper.toEntity(requestDTO))
                .thenReturn(manager);

        when(managerRepo.save(any(Manager.class)))
                .thenReturn(manager);

        when(managerMapper.toResponseDTO(manager))
                .thenReturn(responseDTO);

        ManagerResponseDTO result =
                managerService.update(1L, requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(managerRepo).findById(1L);
        verify(managerRepo).save(any(Manager.class));
    }

    @Test
    @DisplayName("Should delete manager")
    void shouldDeleteManager() {

        doNothing()
                .when(managerRepo)
                .deleteById(1L);

        assertDoesNotThrow(() ->
                managerService.delete(1L));

        verify(managerRepo)
                .deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when manager DTO is null")
    void shouldThrowExceptionWhenManagerDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> managerService.create(null));
    }
}