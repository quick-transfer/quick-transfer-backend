package com.weg.quicktransfer.mapper;

import org.springframework.stereotype.Component;
import java.util.Locale;

import com.weg.quicktransfer.dto.manager.ManagerRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.model.Manager;

@Component
public class ManagerMapper {
    public Manager toEntity(ManagerRequestDTO managerRequestDTO) {
        return new Manager(
            managerRequestDTO.name(),
            managerRequestDTO.username(),
            managerRequestDTO.email(),
            managerRequestDTO.password(),
            Section.valueOf(managerRequestDTO.section().trim().toUpperCase(Locale.ROOT))
        );
    }

    public ManagerResponseDTO toResponse(Manager manager) {
        return new ManagerResponseDTO(
            manager.getId(),
            manager.getName(),
            manager.getUsername(),
            manager.getEmail(),
            manager.getSection().name()
        );
    }
}
