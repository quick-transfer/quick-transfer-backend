package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.coordinator.CoordinatorRequestDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorResponseDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorUpdateRequestDTO;
import com.weg.quicktransfer.exception.CoordinatorNotFoundException;
import com.weg.quicktransfer.mapper.CoordinatorMapper;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.repo.CoordinatorRepository;
import com.weg.quicktransfer.service.CoordinatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class CoordinatorServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CoordinatorRepository coordinatorRepository;

    @Mock
    private CoordinatorMapper coordinatorMapper;

    @InjectMocks
    private CoordinatorService coordinatorService;

    private Coordinator coordinator;
    private CoordinatorResponseDTO responseDTO;
    private CoordinatorRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        coordinator = new Coordinator();
        coordinator.setId(1L);
        coordinator.setName("Coordenador");
        coordinator.setUsername("coord01");
        coordinator.setEmail("coord@dominio.com");
        coordinator.setPassword("123456");

        requestDTO = new CoordinatorRequestDTO("Coordenador", "coord01", "coord@dominio.com", "123456");
        responseDTO = new CoordinatorResponseDTO(1L, "Coordenador", "coord01", "coord@dominio.com");
    }

    @Nested
    @DisplayName("Create Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create coordinator and return response DTO")
        void shouldCreateCoordinator() {
            when(coordinatorMapper.toEntity(requestDTO)).thenReturn(coordinator);
            when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
            when(coordinatorRepository.save(coordinator)).thenReturn(coordinator);
            when(coordinatorMapper.toResponse(coordinator)).thenReturn(responseDTO);

            CoordinatorResponseDTO result = coordinatorService.create(requestDTO);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("Coordenador", result.name());
            assertEquals("coord01", result.username());

            verify(coordinatorMapper).toEntity(requestDTO);
            verify(passwordEncoder).encode("123456");
            verify(coordinatorRepository).save(coordinator);
            verify(coordinatorMapper).toResponse(coordinator);
        }
    }

    @Nested
    @DisplayName("Find Tests")
    class FindTests {

        @Test
        @DisplayName("Should find coordinator by id and return response DTO")
        void shouldFindCoordinatorById() {
            when(coordinatorRepository.findById(1L)).thenReturn(Optional.of(coordinator));
            when(coordinatorMapper.toResponse(coordinator)).thenReturn(responseDTO);

            CoordinatorResponseDTO result = coordinatorService.findById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("Coordenador", result.name());

            verify(coordinatorRepository).findById(1L);
            verify(coordinatorMapper).toResponse(coordinator);
        }

        @Test
        @DisplayName("Should throw CoordinatorNotFoundException when coordinator is not found by id")
        void shouldThrowExceptionWhenFindByIdNotFound() {
            when(coordinatorRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(CoordinatorNotFoundException.class, () -> coordinatorService.findById(1L));

            verify(coordinatorRepository).findById(1L);
            verifyNoInteractions(coordinatorMapper);
        }

        @Test
        @DisplayName("Should return list of all coordinators")
        void shouldFindAllCoordinators() {
            when(coordinatorRepository.findAll()).thenReturn(List.of(coordinator));
            when(coordinatorMapper.toResponse(coordinator)).thenReturn(responseDTO);

            List<CoordinatorResponseDTO> result = coordinatorService.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Coordenador", result.get(0).name());

            verify(coordinatorRepository).findAll();
            verify(coordinatorMapper).toResponse(coordinator);
        }
    }

    @Nested
    @DisplayName("Update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update coordinator fields selectively and return response DTO")
        void shouldUpdateCoordinator() {
            CoordinatorUpdateRequestDTO updateRequest = new CoordinatorUpdateRequestDTO(
                    "Coordenador Atualizado", "coord02", "coord2@dominio.com", "654321"
            );

            CoordinatorResponseDTO updatedResponse = new CoordinatorResponseDTO(
                    1L, "Coordenador Atualizado", "coord02", "coord2@dominio.com"
            );

            when(coordinatorRepository.findById(1L)).thenReturn(Optional.of(coordinator));
            when(passwordEncoder.encode("654321")).thenReturn("encodedNewPassword");
            when(coordinatorRepository.save(coordinator)).thenReturn(coordinator);
            when(coordinatorMapper.toResponse(coordinator)).thenReturn(updatedResponse);

            CoordinatorResponseDTO result = coordinatorService.update(1L, updateRequest);

            assertNotNull(result);
            assertEquals("Coordenador Atualizado", result.name());
            assertEquals("coord02", result.username());

            // Assert that the entity state was mutated in-place
            assertEquals("Coordenador Atualizado", coordinator.getName());
            assertEquals("coord02", coordinator.getUsername());
            assertEquals("coord2@dominio.com", coordinator.getEmail());
            assertEquals("encodedNewPassword", coordinator.getPassword());

            verify(coordinatorRepository).findById(1L);
            verify(passwordEncoder).encode("654321");
            verify(coordinatorRepository).save(coordinator);
            verify(coordinatorMapper).toResponse(coordinator);
        }

        @Test
        @DisplayName("Should throw CoordinatorNotFoundException when updating non-existent coordinator")
        void shouldThrowExceptionWhenUpdateNotFound() {
            CoordinatorUpdateRequestDTO updateRequest = new CoordinatorUpdateRequestDTO(
                    "Coordenador", "coord01", "coord@dominio.com", "123456"
            );

            when(coordinatorRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(CoordinatorNotFoundException.class, () -> coordinatorService.update(1L, updateRequest));

            verify(coordinatorRepository).findById(1L);
            verify(coordinatorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete coordinator when exists")
        void shouldDeleteCoordinator() {
            when(coordinatorRepository.existsById(1L)).thenReturn(true);
            doNothing().when(coordinatorRepository).deleteById(1L);

            assertDoesNotThrow(() -> coordinatorService.delete(1L));

            verify(coordinatorRepository).existsById(1L);
            verify(coordinatorRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw CoordinatorNotFoundException when deleting non-existent coordinator")
        void shouldThrowExceptionWhenDeleteNotFound() {
            when(coordinatorRepository.existsById(1L)).thenReturn(false);

            assertThrows(CoordinatorNotFoundException.class, () -> coordinatorService.delete(1L));

            verify(coordinatorRepository).existsById(1L);
            verify(coordinatorRepository, never()).deleteById(anyLong());
        }
    }
}