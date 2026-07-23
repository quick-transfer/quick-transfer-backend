package com.weg.quicktransfer.mapper;

import org.springframework.stereotype.Component;

import com.weg.quicktransfer.dto.place.PlaceRequestDTO;
import com.weg.quicktransfer.dto.place.PlaceResponseDTO;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.model.Place;

@Component
public class PlaceMapper {
    public Place toEntity(PlaceRequestDTO placeRequestDTO) {
        return new Place(
            placeRequestDTO.placeName(),
            Park.valueOf(placeRequestDTO.park()),
            Section.valueOf(placeRequestDTO.section())
        );
    }

    public PlaceResponseDTO toResponse(Place place) {
        return new PlaceResponseDTO(
            place.getId(),
            place.getPlaceName(),
            place.getPark().name(),
            place.getSection().name()
        );
    }
}
