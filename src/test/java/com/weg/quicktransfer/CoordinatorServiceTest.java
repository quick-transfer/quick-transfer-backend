package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.coordinator.CoordinatorFilter;
import com.weg.quicktransfer.dto.coordinator.CoordinatorRequestDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorResponseDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorUpdateRequestDTO;
import com.weg.quicktransfer.exception.CoordinatorNotFoundException;
import com.weg.quicktransfer.exception.UserNotAllowdException;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.CoordinatorMapper;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repo.CoordinatorRepository;
import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.service.CoordinatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class CoordinatorServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CoordinatorRepository coordinatorRepository;

    @Mock
    private CoordinatorMapper coordinatorMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CoordinatorService coordinatorService;

    private Coordinator coordinator;
    private CoordinatorResponseDTO responseDTO;
    private CoordinatorRequestDTO requestDTO;

    private static final UUID COORDINATOR_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID NON_EXISTENT_COORDINATOR_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174999");
    private static final UUID ADMIN_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174111");

    @BeforeEach
    void setUp() {
        coordinator = new Coordinator();
        coordinator.setId(COORDINATOR_ID);
        coordinator.setName("Coordenador");
        coordinator.setUsername("coord01");
        coordinator.setEmail("coord@dominio.com");
        coordinator.setPassword("123456");

        requestDTO = new CoordinatorRequestDTO("Coordenador", "coord01", "coord@dominio.com", "123456");
        responseDTO = new CoordinatorResponseDTO(COORDINATOR_ID, "Coordenador", "coord01", "coord@dominio.com");
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
            assertEquals(COORDINATOR_ID, result.id());
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
            when(coordinatorRepository.findById(COORDINATOR_ID)).thenReturn(Optional.of(coordinator));
            when(coordinatorMapper.toResponse(coordinator)).thenReturn(responseDTO);

            CoordinatorResponseDTO result = coordinatorService.findById(COORDINATOR_ID);

            assertNotNull(result);
            assertEquals(COORDINATOR_ID, result.id());
            assertEquals("Coordenador", result.name());

            verify(coordinatorRepository).findById(COORDINATOR_ID);
            verify(coordinatorMapper).toResponse(coordinator);
        }

        @Test
        @DisplayName("Should throw CoordinatorNotFoundException when coordinator is not found by id")
        void shouldThrowExceptionWhenFindByIdNotFound() {
            when(coordinatorRepository.findById(NON_EXISTENT_COORDINATOR_ID)).thenReturn(Optional.empty());

            assertThrows(CoordinatorNotFoundException.class, () -> coordinatorService.findById(NON_EXISTENT_COORDINATOR_ID));

            verify(coordinatorRepository).findById(NON_EXISTENT_COORDINATOR_ID);
            verifyNoInteractions(coordinatorMapper);
        }

        @Test
        @DisplayName("Should find coordinator by name when username is not found")
        void shouldFindCoordinatorByName() {
            when(coordinatorRepository.findFirstByUsername("Coordenador")).thenReturn(Optional.empty());
            when(coordinatorRepository.findFirstByName("Coordenador")).thenReturn(Optional.of(coordinator));
            when(coordinatorMapper.toResponse(coordinator)).thenReturn(responseDTO);

            CoordinatorResponseDTO result = coordinatorService.findByName("Coordenador");

            assertNotNull(result);
            assertEquals("Coordenador", result.name());

            verify(coordinatorRepository).findFirstByUsername("Coordenador");
            verify(coordinatorRepository).findFirstByName("Coordenador");
            verify(coordinatorMapper).toResponse(coordinator);
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

        @Test
        @DisplayName("Should search coordinators using filter specification")
        @SuppressWarnings("unchecked")
        void shouldSearchCoordinatorsWithFilter() {
            CoordinatorFilter filter = new CoordinatorFilter("Coordenador", "coord01");

            when(coordinatorRepository.findAll(any(Specification.class))).thenReturn(List.of(coordinator));
            when(coordinatorMapper.toResponse(coordinator)).thenReturn(responseDTO);

            List<CoordinatorResponseDTO> result = coordinatorService.searchCoordinators(filter);

            assertNotNull(result);
            assertEquals(1, result.size());

            verify(coordinatorRepository).findAll(any(Specification.class));
            verify(coordinatorMapper).toResponse(coordinator);
        }
    }

    @Nested
    @DisplayName("Update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update coordinator fields selectively and return response DTO when user is Admin")
        void shouldUpdateCoordinatorWhenUserIsAdmin() {
            CoordinatorUpdateRequestDTO updateRequest = new CoordinatorUpdateRequestDTO(
                    "Coordenador Atualizado", "654321"
            );

            CoordinatorResponseDTO updatedResponse = new CoordinatorResponseDTO(
                    COORDINATOR_ID, "Coordenador Atualizado", "coord01", "coord@dominio.com"
            );

            Admin adminUser = new Admin();
            adminUser.setId(ADMIN_USER_ID);

            when(userRepository.findById(ADMIN_USER_ID)).thenReturn(Optional.of(adminUser));
            when(coordinatorRepository.findById(COORDINATOR_ID)).thenReturn(Optional.of(coordinator));
            when(passwordEncoder.encode("654321")).thenReturn("encodedNewPassword");
            when(coordinatorRepository.save(coordinator)).thenReturn(coordinator);
            when(coordinatorMapper.toResponse(coordinator)).thenReturn(updatedResponse);

            CoordinatorResponseDTO result = coordinatorService.update(COORDINATOR_ID, updateRequest, ADMIN_USER_ID);

            assertNotNull(result);
            assertEquals("Coordenador Atualizado", result.name());

            // Assert that the entity state was mutated in-place
            assertEquals("Coordenador Atualizado", coordinator.getName());
            assertEquals("encodedNewPassword", coordinator.getPassword());

            verify(userRepository).findById(ADMIN_USER_ID);
            verify(coordinatorRepository).findById(COORDINATOR_ID);
            verify(passwordEncoder).encode("654321");
            verify(coordinatorRepository).save(coordinator);
            verify(coordinatorMapper).toResponse(coordinator);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user performing update is not logged in")
        void shouldThrowExceptionWhenUserNotLogged() {
            CoordinatorUpdateRequestDTO updateRequest = new CoordinatorUpdateRequestDTO(
                    "Coordenador Atualizado", "654321"
            );

            when(userRepository.findById(ADMIN_USER_ID)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> coordinatorService.update(COORDINATOR_ID, updateRequest, ADMIN_USER_ID));

            verify(userRepository).findById(ADMIN_USER_ID);
            verify(coordinatorRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw CoordinatorNotFoundException when updating non-existent coordinator")
        void shouldThrowExceptionWhenUpdateNotFound() {
            CoordinatorUpdateRequestDTO updateRequest = new CoordinatorUpdateRequestDTO(
                    "Coordenador", "123456"
            );

            Admin adminUser = new Admin();
            adminUser.setId(ADMIN_USER_ID);

            when(userRepository.findById(ADMIN_USER_ID)).thenReturn(Optional.of(adminUser));
            when(coordinatorRepository.findById(NON_EXISTENT_COORDINATOR_ID)).thenReturn(Optional.empty());

            assertThrows(CoordinatorNotFoundException.class,
                    () -> coordinatorService.update(NON_EXISTENT_COORDINATOR_ID, updateRequest, ADMIN_USER_ID));

            verify(coordinatorRepository).findById(NON_EXISTENT_COORDINATOR_ID);
            verify(coordinatorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete coordinator when exists")
        void shouldDeleteCoordinator() {
            when(coordinatorRepository.existsById(COORDINATOR_ID)).thenReturn(true);
            doNothing().when(coordinatorRepository).deleteById(COORDINATOR_ID);

            assertDoesNotThrow(() -> coordinatorService.delete(COORDINATOR_ID));

            verify(coordinatorRepository).existsById(COORDINATOR_ID);
            verify(coordinatorRepository).deleteById(COORDINATOR_ID);
        }

        @Test
        @DisplayName("Should throw CoordinatorNotFoundException when deleting non-existent coordinator")
        void shouldThrowExceptionWhenDeleteNotFound() {
            when(coordinatorRepository.existsById(NON_EXISTENT_COORDINATOR_ID)).thenReturn(false);

            assertThrows(CoordinatorNotFoundException.class,
                    () -> coordinatorService.delete(NON_EXISTENT_COORDINATOR_ID));

            verify(coordinatorRepository).existsById(NON_EXISTENT_COORDINATOR_ID);
            verify(coordinatorRepository, never()).deleteById(any(UUID.class));
        }
    }
}