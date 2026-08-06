package com.weg.quicktransfer.dto.place;

import java.util.UUID;

public record PlaceResponseDTO(
    UUID id,

    String placeName,

    String park,
    
    String section,

    String code,

    String description,

    String city,

    String state,

    String status
) {
    public PlaceResponseDTO(UUID id, String placeName, String park, String section) {
        this(id, placeName, park, section, null, null, null, null, "ACTIVE");
    }
}
