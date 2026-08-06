package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.transfer.TransferRequestResolveDTO;
import com.weg.quicktransfer.dto.transfer.TransferRequestResponseDTO;
import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.enums.TransferRequestStatus;
import com.weg.quicktransfer.mapper.ShiftTransferRequestMapper;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.model.OperationalShift;
import com.weg.quicktransfer.model.ShiftTransferRequest;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.OperationalShiftRepository;
import com.weg.quicktransfer.repo.ShiftTransferRequestRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.security.UserPrincipal;
import com.weg.quicktransfer.service.ShiftTransferRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftTransferRequestServiceTest {
    @Mock private ShiftTransferRequestRepository requestRepository;
    @Mock private OperationalShiftRepository shiftRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private UserRepository userRepository;
    @Mock private ShiftTransferRequestMapper mapper;
    @InjectMocks private ShiftTransferRequestService service;

    private UUID requestId;
    private OperationalShift currentShift;
    private OperationalShift targetShift;
    private Student student;
    private Admin actor;
    private UserPrincipal principal;
    private ShiftTransferRequest request;

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();
        currentShift = shift(UUID.randomUUID(), "TRN-A", 50);
        targetShift = shift(UUID.randomUUID(), "TRN-B", 2);

        student = new Student();
        student.setId(UUID.randomUUID());
        student.setOperationalShift(currentShift);

        actor = new Admin();
        actor.setId(UUID.randomUUID());
        actor.setName("Administrador");
        actor.setUsername("admin");
        actor.setPassword("secret");
        actor.setEmail("admin@example.com");
        actor.setRole(Role.ADMIN);
        actor.setActive(true);
        principal = new UserPrincipal(actor);

        request = new ShiftTransferRequest();
        request.setId(requestId);
        request.setStudent(student);
        request.setCurrentShift(currentShift);
        request.setTargetShift(targetShift);
        request.setRequestedBy(actor);
        request.setReason("Compatibilidade de horário");
        request.setStatus(TransferRequestStatus.PENDING);
    }

    @Test
    void shouldMoveStudentWhenRequestIsApproved() {
        TransferRequestResponseDTO response = response("APPROVED");
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(shiftRepository.findByIdForUpdate(targetShift.getId())).thenReturn(Optional.of(targetShift));
        when(studentRepository.countByOperationalShiftId(targetShift.getId())).thenReturn(1L);
        when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));
        when(requestRepository.save(request)).thenReturn(request);
        when(mapper.toResponse(request)).thenReturn(response);

        TransferRequestResponseDTO result = service.resolve(
                requestId, new TransferRequestResolveDTO("APPROVED", "Capacidade disponível"), principal);

        assertSame(response, result);
        assertSame(targetShift, student.getOperationalShift());
        assertEquals(TransferRequestStatus.APPROVED, request.getStatus());
        assertSame(actor, request.getResolvedBy());
        verify(studentRepository).save(student);
    }

    @Test
    void shouldRejectWithoutMovingStudent() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));
        when(requestRepository.save(request)).thenReturn(request);
        when(mapper.toResponse(request)).thenReturn(response("REJECTED"));

        service.resolve(requestId, new TransferRequestResolveDTO("REJECTED", "Sem disponibilidade"), principal);

        assertSame(currentShift, student.getOperationalShift());
        assertEquals(TransferRequestStatus.REJECTED, request.getStatus());
        verify(studentRepository, never()).save(any());
    }

    @Test
    void shouldPreventApprovalWhenTargetShiftIsFull() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(shiftRepository.findByIdForUpdate(targetShift.getId())).thenReturn(Optional.of(targetShift));
        when(studentRepository.countByOperationalShiftId(targetShift.getId())).thenReturn(2L);
        when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));

        assertThrows(IllegalArgumentException.class, () -> service.resolve(
                requestId, new TransferRequestResolveDTO("APPROVED", null), principal));

        assertSame(currentShift, student.getOperationalShift());
        assertEquals(TransferRequestStatus.PENDING, request.getStatus());
        verify(requestRepository, never()).save(any());
    }

    private OperationalShift shift(UUID id, String code, int capacity) {
        OperationalShift shift = new OperationalShift();
        shift.setId(id);
        shift.setCode(code);
        shift.setName(code);
        shift.setCapacity(capacity);
        shift.setActive(true);
        return shift;
    }

    private TransferRequestResponseDTO response(String status) {
        return new TransferRequestResponseDTO(
                requestId, student.getId(), null, null,
                currentShift.getId(), currentShift.getName(),
                targetShift.getId(), targetShift.getName(),
                request.getReason(), status, null,
                actor.getId(), actor.getName(), null, null, null, null);
    }
}
