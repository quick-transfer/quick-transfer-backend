package com.weg.quicktransfer.mapper;

import com.weg.quicktransfer.dto.coordinator.CoordinatorRequestDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorResponseDTO;
import com.weg.quicktransfer.model.Coordinator;
import org.springframework.stereotype.Component;

@Component
public class CoordinatorMapper {
    // transforming coordinator request to entity coordinator
    public Coordinator toEntity(CoordinatorRequestDTO coordinatorRequestDTO){
        return new Coordinator(
                coordinatorRequestDTO.name(),
                coordinatorRequestDTO.username(),
                coordinatorRequestDTO.email(),
                coordinatorRequestDTO.password()
        );
    }
    // transforming entity coordinator to coordinator response
    public CoordinatorResponseDTO toResponse(Coordinator coordinator){
        return new CoordinatorResponseDTO(
                coordinator.getId(),
                coordinator.getName(),
                coordinator.getUsername(),
                coordinator.getEmail()
        );
    }
}
