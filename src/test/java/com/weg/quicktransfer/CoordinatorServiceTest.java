// package com.weg.quicktransfer;

// import com.weg.quicktransfer.dto.CoordinatorRequestDTO;
// import com.weg.quicktransfer.dto.CoordinatorResponseDTO;
// import com.weg.quicktransfer.mapper.CoordinatorMapper;
// import com.weg.quicktransfer.model.Coordinator;
// import com.weg.quicktransfer.repo.CoordinatorRepository;
// import com.weg.quicktransfer.service.CoordinatorService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class CoordinatorServiceTest {

//     @Mock
//     private CoordinatorRepository coordinatorRepository;

//     @Mock
//     private CoordinatorMapper coordinatorMapper;

//     @InjectMocks
//     private CoordinatorService coordinatorService;

//     private Coordinator coordinator;
//     private CoordinatorRequestDTO requestDTO;
//     private CoordinatorResponseDTO responseDTO;

//     @BeforeEach
//     void setUp() {
//         coordinator = new Coordinator();
//         coordinator.setId(1L);
//         coordinator.setName("Coordenador");
//         coordinator.setUsername("coord01");
//         coordinator.setEmail("coord@dominio.com");
//         coordinator.setPassword("123456");

//         requestDTO = new CoordinatorRequestDTO("Coordenador", "coord01", "coord@dominio.com", "123456");
//         responseDTO = new CoordinatorResponseDTO(1L, "Coordenador", "coord01", "coord@dominio.com");
//     }

//     @Test
//     @DisplayName("Should create coordinator and return response dto")
//     void shouldCreateCoordinator() {
//         when(coordinatorMapper.toEntity(requestDTO)).thenReturn(coordinator);
//         when(coordinatorRepository.save(coordinator)).thenReturn(coordinator);
//         when(coordinatorMapper.toResponse(coordinator)).thenReturn(responseDTO);

//         CoordinatorResponseDTO result = coordinatorService.create(requestDTO);

//         assertNotNull(result);
//         assertEquals(1L, result.id());
//         assertEquals("Coordenador", result.name());
//         assertEquals("coord01", result.username());

//         verify(coordinatorMapper).toEntity(requestDTO);
//         verify(coordinatorRepository).save(coordinator);
//         verify(coordinatorMapper).toResponse(coordinator);
//     }

//     @Test
//     @DisplayName("Should find coordinator by id and return response dto")
//     void shouldFindCoordinatorById() {
//         when(coordinatorRepository.findById(1L)).thenReturn(Optional.of(coordinator));
//         when(coordinatorMapper.toResponse(coordinator)).thenReturn(responseDTO);

//         CoordinatorResponseDTO result = coordinatorService.findById(1L);

//         assertNotNull(result);
//         assertEquals(1L, result.id());
//         assertEquals("Coordenador", result.name());

//         verify(coordinatorRepository).findById(1L);
//         verify(coordinatorMapper).toResponse(coordinator);
//     }

//     @Test
//     @DisplayName("Should update coordinator and return response dto")
//     void shouldUpdateCoordinator() {
//         CoordinatorRequestDTO updatedRequest = new CoordinatorRequestDTO("Coordenador Atualizado", "coord02", "coord2@dominio.com", "123456");

//         Coordinator updatedEntity = new Coordinator();
//         updatedEntity.setId(1L);
//         updatedEntity.setName("Coordenador Atualizado");
//         updatedEntity.setUserName("coord02");
//         updatedEntity.setEmail("coord2@dominio.com");
//         updatedEntity.setPassword("123456");

//         CoordinatorResponseDTO updatedResponse = new CoordinatorResponseDTO(1L, "Coordenador Atualizado", "coord02", "coord2@dominio.com");

//         when(coordinatorRepository.findById(1L)).thenReturn(Optional.of(coordinator));
//         when(coordinatorMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
//         when(coordinatorRepository.save(any(Coordinator.class))).thenReturn(updatedEntity);
//         when(coordinatorMapper.toResponse(updatedEntity)).thenReturn(updatedResponse);

//         CoordinatorResponseDTO result = coordinatorService.update(1L, updatedRequest);

//         assertNotNull(result);
//         assertEquals("Coordenador Atualizado", result.name());
//         assertEquals("coord02", result.username());

//         verify(coordinatorRepository).findById(1L);
//         verify(coordinatorMapper).toEntity(updatedRequest);
//         verify(coordinatorRepository).save(any(Coordinator.class));
//         verify(coordinatorMapper).toResponse(updatedEntity);
//     }

//     @Test
//     @DisplayName("Should delete coordinator")
//     void shouldDeleteCoordinator() {
//         doNothing().when(coordinatorRepository).deleteById(1L);

//         assertDoesNotThrow(() -> coordinatorService.delete(1L));

//         verify(coordinatorRepository).deleteById(1L);
//     }

//     @Test
//     @DisplayName("Should throw exception when request dto is null")
//     void shouldThrowExceptionWhenRequestDtoIsNull() {
//         assertThrows(IllegalArgumentException.class, () -> coordinatorService.create(null));
//     }
// }