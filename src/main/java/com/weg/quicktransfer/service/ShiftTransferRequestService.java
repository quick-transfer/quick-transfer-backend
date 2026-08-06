package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.transfer.TransferRequestCreateDTO;
import com.weg.quicktransfer.dto.transfer.TransferRequestResolveDTO;
import com.weg.quicktransfer.dto.transfer.TransferRequestResponseDTO;
import com.weg.quicktransfer.enums.TransferRequestStatus;
import com.weg.quicktransfer.exception.ResourceNotFoundException;
import com.weg.quicktransfer.exception.StudentNotFoundException;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.ShiftTransferRequestMapper;
import com.weg.quicktransfer.model.OperationalShift;
import com.weg.quicktransfer.model.ShiftTransferRequest;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repo.OperationalShiftRepository;
import com.weg.quicktransfer.repo.ShiftTransferRequestRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShiftTransferRequestService {
    private final ShiftTransferRequestRepository requestRepository;
    private final OperationalShiftRepository shiftRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ShiftTransferRequestMapper mapper;

    @Transactional
    public TransferRequestResponseDTO create(
            TransferRequestCreateDTO input,
            UserPrincipal principal) {
        Student student = findStudent(input.studentId());
        OperationalShift target = findShift(input.targetShiftId());
        validateNewRequest(student, target);

        ShiftTransferRequest request = baseRequest(student, target, input.reason(), principal);
        return mapper.toResponse(requestRepository.save(request));
    }

    @Transactional
    public TransferRequestResponseDTO assignManually(
            UUID studentId,
            UUID targetShiftId,
            String reason,
            UserPrincipal principal) {
        Student student = findStudent(studentId);
        OperationalShift target = shiftRepository.findByIdForUpdate(targetShiftId)
                .orElseThrow(() -> shiftNotFound(targetShiftId));
        validateDifferentShift(student, target);
        if (requestRepository.existsByStudentIdAndStatus(studentId, TransferRequestStatus.PENDING)) {
            throw new IllegalArgumentException("Student already has a pending transfer request");
        }
        validateCapacity(target);

        ShiftTransferRequest request = baseRequest(
                student,
                target,
                reason == null || reason.isBlank() ? "Ajuste manual de turno" : reason,
                principal);
        User actor = request.getRequestedBy();
        request.setStatus(TransferRequestStatus.APPROVED);
        request.setResolvedBy(actor);
        request.setResolvedAt(LocalDateTime.now());
        request.setResolutionNotes("Ajuste manual aprovado automaticamente");
        student.setOperationalShift(target);
        studentRepository.save(student);
        return mapper.toResponse(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public Page<TransferRequestResponseDTO> findAll(String status, Pageable pageable) {
        Page<ShiftTransferRequest> page = status == null || status.isBlank()
                ? requestRepository.findAll(pageable)
                : requestRepository.findAllByStatus(parseStatus(status), pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional
    public TransferRequestResponseDTO resolve(
            UUID id,
            TransferRequestResolveDTO input,
            UserPrincipal principal) {
        ShiftTransferRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer request not found with ID: " + id));
        if (request.getStatus() != TransferRequestStatus.PENDING) {
            throw new IllegalArgumentException("Transfer request has already been resolved");
        }

        TransferRequestStatus status = parseStatus(input.status());
        if (status == TransferRequestStatus.PENDING) {
            throw new IllegalArgumentException("Resolution status must be APPROVED or REJECTED");
        }

        User actor = findUser(principal);
        if (status == TransferRequestStatus.APPROVED) {
            if (request.getStudent().getOperationalShift() == null
                    || !request.getStudent().getOperationalShift().getId()
                            .equals(request.getCurrentShift().getId())) {
                throw new IllegalArgumentException("Student current shift has changed since the request was created");
            }
            OperationalShift target = shiftRepository.findByIdForUpdate(request.getTargetShift().getId())
                    .orElseThrow(() -> shiftNotFound(request.getTargetShift().getId()));
            validateCapacity(target);
            request.getStudent().setOperationalShift(target);
            studentRepository.save(request.getStudent());
        }

        request.setStatus(status);
        request.setResolvedBy(actor);
        request.setResolvedAt(LocalDateTime.now());
        request.setResolutionNotes(input.resolutionNotes());
        return mapper.toResponse(requestRepository.save(request));
    }

    private ShiftTransferRequest baseRequest(
            Student student,
            OperationalShift target,
            String reason,
            UserPrincipal principal) {
        if (student.getOperationalShift() == null) {
            throw new IllegalArgumentException("Student does not have an operational shift");
        }
        ShiftTransferRequest request = new ShiftTransferRequest();
        request.setStudent(student);
        request.setCurrentShift(student.getOperationalShift());
        request.setTargetShift(target);
        request.setRequestedBy(findUser(principal));
        request.setReason(reason.trim());
        request.setStatus(TransferRequestStatus.PENDING);
        return request;
    }

    private void validateNewRequest(Student student, OperationalShift target) {
        validateDifferentShift(student, target);
        if (requestRepository.existsByStudentIdAndStatus(student.getId(), TransferRequestStatus.PENDING)) {
            throw new IllegalArgumentException("Student already has a pending transfer request");
        }
    }

    private void validateDifferentShift(Student student, OperationalShift target) {
        if (student.getOperationalShift() == null) {
            throw new IllegalArgumentException("Student does not have an operational shift");
        }
        if (student.getOperationalShift().getId().equals(target.getId())) {
            throw new IllegalArgumentException("Target shift must be different from current shift");
        }
        if (!Boolean.TRUE.equals(target.getActive())) {
            throw new IllegalArgumentException("Target shift is inactive");
        }
    }

    private void validateCapacity(OperationalShift target) {
        long occupancy = studentRepository.countByOperationalShiftId(target.getId());
        if (occupancy >= target.getCapacity()) {
            throw new IllegalArgumentException("Target shift has reached its capacity");
        }
    }

    private Student findStudent(UUID id) {
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    private OperationalShift findShift(UUID id) {
        return shiftRepository.findById(id).orElseThrow(() -> shiftNotFound(id));
    }

    private ResourceNotFoundException shiftNotFound(UUID id) {
        return new ResourceNotFoundException("Operational shift not found with ID: " + id);
    }

    private User findUser(UserPrincipal principal) {
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(principal.getId()));
    }

    private TransferRequestStatus parseStatus(String status) {
        try {
            return TransferRequestStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Invalid transfer request status");
        }
    }
}
