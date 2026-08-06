package com.weg.quicktransfer.dto.vacancy;

import java.util.List;
import java.util.UUID;

public record VacancyResponseDTO(
    UUID id,

    String name,

    String description,

    Long numbersVacancies,

    String area,

    String shift,

    String park,
    
    String section,

    List<VacancySkillResponseDTO> skills,

    String placeName,

    String status,

    UUID managerId,

    String managerName,

    long filledSpots,

    UUID placeId
) {
    public VacancyResponseDTO(UUID id, String name, String description, Long numbersVacancies,
            String area, String shift, String park, String section,
            List<VacancySkillResponseDTO> skills) {
        this(id, name, description, numbersVacancies, area, shift, park, section, skills,
                null, "OPEN", null, null, 0, null);
    }
}
