package com.weg.quicktransfer.mapper;

import org.springframework.stereotype.Component;
import java.util.Locale;

import com.weg.quicktransfer.dto.place.PlaceRequestDTO;
import com.weg.quicktransfer.dto.place.PlaceResponseDTO;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.enums.EntityStatus;
import com.weg.quicktransfer.model.Place;

@Component
public class PlaceMapper {
    public Place toEntity(PlaceRequestDTO placeRequestDTO) {
        return new Place(
            placeRequestDTO.placeName(),
            normalizeCode(placeRequestDTO.code(), placeRequestDTO.placeName()),
            placeRequestDTO.description(),
            placeRequestDTO.city(),
            normalizeState(placeRequestDTO.state()),
            parseStatus(placeRequestDTO.status()),
            Park.valueOf(placeRequestDTO.park().trim().toUpperCase(Locale.ROOT)),
            Section.valueOf(placeRequestDTO.section().trim().toUpperCase(Locale.ROOT))
        );
    }

    public PlaceResponseDTO toResponse(Place place) {
        return new PlaceResponseDTO(
            place.getId(),
            place.getPlaceName(),
            place.getPark().name(),
            place.getSection().name(),
            place.getCode(),
            place.getDescription(),
            place.getCity(),
            place.getState(),
            place.getStatus().name()
        );
    }

    public String normalizeCode(String code, String fallback) {
        String source = code == null || code.isBlank() ? fallback : code;
        return source.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    public String normalizeState(String state) {
        return state == null || state.isBlank() ? null : state.trim().toUpperCase(Locale.ROOT);
    }

    public EntityStatus parseStatus(String status) {
        return status == null || status.isBlank()
                ? EntityStatus.ACTIVE
                : EntityStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
    }
}
