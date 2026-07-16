package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.CoordinatorRequestDTO;
import com.weg.quicktransfer.dto.CoordinatorResponseDTO;
import com.weg.quicktransfer.mapper.CoordinatorMapper;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.repo.CoordinatorRepo;
import com.weg.quicktransfer.service.CoordinatorService;
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
class CoordinatorServiceTest {

    @Mock
    private CoordinatorRepo coordinatorRepo;

    @Mock
    private CoordinatorMapper coordinatorMapper;

    @InjectMocks
    private CoordinatorService coordinatorService;

    private Coordinator coordinator;
    private CoordinatorRequestDTO requestDTO;
    private CoordinatorResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        coordinator = new Coordinator();
        coordinator.setId(1L);
        coordinator.setName("Coordenador");
        coordinator.setUsername("coord01");
        coordinator.setEmail("coord@dominio.com");
        coordinator.setPassword("123456");

        requestDTO = new CoordinatorRequestDTO();
        requestDTO.setName("Coordenador");
        requestDTO.setUsername("coord01");
        requestDTO.setEmail("coord@dominio.com");
        requestDTO.setPassword("123456");

        responseDTO = new CoordinatorResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Coordenador");
        responseDTO.setUsername("coord01");
        responseDTO.setEmail("coord@dominio.com");
    }

    @Test
    @DisplayName("Should create coordinator and return response dto")
    void shouldCreateCoordinator() {
        when(coordinatorMapper.toEntity(requestDTO)).thenReturn(coordinator);
        when(coordinatorRepo.save(coordinator)).thenReturn(coordinator);
        when(coordinatorMapper.toResponseDTO(coordinator)).thenReturn(responseDTO);

        CoordinatorResponseDTO result = coordinatorService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Coordenador", result.getName());
        assertEquals("coord01", result.getUsername());

        verify(coordinatorMapper).toEntity(requestDTO);
        verify(coordinatorRepo).save(coordinator);
        verify(coordinatorMapper).toResponseDTO(coordinator);
    }

    @Test
    @DisplayName("Should find coordinator by id and return response dto")
    void shouldFindCoordinatorById() {
        when(coordinatorRepo.findById(1L)).thenReturn(coordinator);
        when(coordinatorMapper.toResponseDTO(coordinator)).thenReturn(responseDTO);

        CoordinatorResponseDTO result = coordinatorService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Coordenador", result.getName());

        verify(coordinatorRepo).findById(1L);
        verify(coordinatorMapper).toResponseDTO(coordinator);
    }

    @Test
    @DisplayName("Should update coordinator and return response dto")
    void shouldUpdateCoordinator() {
        CoordinatorRequestDTO updatedRequest = new CoordinatorRequestDTO();
        updatedRequest.setName("Coordenador Atualizado");
        updatedRequest.setUsername("coord02");
        updatedRequest.setEmail("coord2@dominio.com");
        updatedRequest.setPassword("123456");

        Coordinator updatedEntity = new Coordinator();
        updatedEntity.setId(1L);
        updatedEntity.setName("Coordenador Atualizado");
        updatedEntity.setUsername("coord02");
        updatedEntity.setEmail("coord2@dominio.com");
        updatedEntity.setPassword("123456");

        CoordinatorResponseDTO updatedResponse = new CoordinatorResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Coordenador Atualizado");
        updatedResponse.setUsername("coord02");
        updatedResponse.setEmail("coord2@dominio.com");

        when(coordinatorRepo.findById(1L)).thenReturn(coordinator);
        when(coordinatorMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
        when(coordinatorRepo.save(any(Coordinator.class))).thenReturn(updatedEntity);
        when(coordinatorMapper.toResponseDTO(updatedEntity)).thenReturn(updatedResponse);

        CoordinatorResponseDTO result = coordinatorService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals("Coordenador Atualizado", result.getName());
        assertEquals("coord02", result.getUsername());

        verify(coordinatorRepo).findById(1L);
        verify(coordinatorMapper).toEntity(updatedRequest);
        verify(coordinatorRepo).save(any(Coordinator.class));
        verify(coordinatorMapper).toResponseDTO(updatedEntity);
    }

    @Test
    @DisplayName("Should delete coordinator")
    void shouldDeleteCoordinator() {
        doNothing().when(coordinatorRepo).deleteById(1L);

        assertDoesNotThrow(() -> coordinatorService.delete(1L));

        verify(coordinatorRepo).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> coordinatorService.create(null));
    }
}