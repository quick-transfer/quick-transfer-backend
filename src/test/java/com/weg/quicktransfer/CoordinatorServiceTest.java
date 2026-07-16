package com.weg.quicktransfer;

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

    @InjectMocks
    private CoordinatorService coordinatorService;

    private Coordinator coordinator;

    @BeforeEach
    void setUp() {
        coordinator = new Coordinator();
        coordinator.setId(1L);
    }

    @Test
    @DisplayName("Should create coordinator")
    void shouldCreateCoordinator() {
        when(coordinatorRepo.create(any(Coordinator.class)))
                .thenReturn(coordinator);

        Coordinator result = coordinatorRepo.create(coordinator);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(coordinatorRepo, times(1))
                .create(coordinator);
    }

    @Test
    @DisplayName("Should find coordinator by id")
    void shouldFindCoordinatorById() {
        when(coordinatorRepo.findById(1L))
                .thenReturn(coordinator);

        Coordinator result = coordinatorRepo.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(coordinatorRepo, times(1))
                .findById(1L);
    }

    @Test
    @DisplayName("Should update coordinator")
    void shouldUpdateCoordinator() {
        Coordinator updatedCoordinator = new Coordinator();
        updatedCoordinator.setId(1L);

        when(coordinatorRepo.findById(1L))
                .thenReturn(coordinator);

        when(coordinatorRepo.create(any(Coordinator.class)))
                .thenReturn(updatedCoordinator);

        Coordinator result = coordinatorRepo.update(1L, updatedCoordinator);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(coordinatorRepo, times(1))
                .findById(1L);

        verify(coordinatorRepo, times(1))
                .create(any(Coordinator.class));
    }

    @Test
    @DisplayName("Should delete coordinator")
    void shouldDeleteCoordinator() {
        when(coordinatorRepo.findById(1L))
                .thenReturn(coordinator);

        doNothing().when(coordinatorRepo)
                .deleteById(1L);

        assertDoesNotThrow(() -> coordinatorRepo.delete(1L));

        verify(coordinatorRepo, times(1))
                .deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when coordinator is null")
    void shouldThrowExceptionWhenCoordinatorIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> coordinatorService.create(null));
    }
}