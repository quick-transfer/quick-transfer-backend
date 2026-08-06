package com.weg.quicktransfer.mapper;

import com.weg.quicktransfer.dto.application.VacancyApplicationResponseDTO;
import com.weg.quicktransfer.model.VacancyApplication;
import org.springframework.stereotype.Component;

@Component
public class VacancyApplicationMapper {
    public VacancyApplicationResponseDTO toResponse(VacancyApplication application) {
        return new VacancyApplicationResponseDTO(
                application.getId(),
                application.getVacancy().getId(),
                application.getVacancy().getName(),
                application.getStudent().getId(),
                application.getStudent().getName(),
                application.getStudent().getEmail(),
                application.getStudent().getRegistration(),
                application.getCoordinator().getId(),
                application.getCoordinator().getName(),
                application.getVacancy().getManager() == null
                        ? null
                        : application.getVacancy().getManager().getId(),
                application.getVacancy().getManager() == null
                        ? null
                        : application.getVacancy().getManager().getName(),
                application.getStatus().name(),
                application.getNotes(),
                application.getCreatedAt(),
                application.getUpdatedAt(),
                application.getInterview() == null ? null : application.getInterview().getId());
    }
}
