package com.weg.quicktransfer.mapper;

import com.weg.quicktransfer.dto.transfer.TransferRequestResponseDTO;
import com.weg.quicktransfer.model.ShiftTransferRequest;
import org.springframework.stereotype.Component;

@Component
public class ShiftTransferRequestMapper {
    public TransferRequestResponseDTO toResponse(ShiftTransferRequest request) {
        return new TransferRequestResponseDTO(
                request.getId(),
                request.getStudent().getId(),
                request.getStudent().getName(),
                request.getStudent().getRegistration(),
                request.getCurrentShift().getId(),
                request.getCurrentShift().getName(),
                request.getTargetShift().getId(),
                request.getTargetShift().getName(),
                request.getReason(),
                request.getStatus().name(),
                request.getRequestedAt(),
                request.getRequestedBy().getId(),
                request.getRequestedBy().getName(),
                request.getResolvedAt(),
                request.getResolvedBy() == null ? null : request.getResolvedBy().getId(),
                request.getResolvedBy() == null ? null : request.getResolvedBy().getName(),
                request.getResolutionNotes());
    }
}
