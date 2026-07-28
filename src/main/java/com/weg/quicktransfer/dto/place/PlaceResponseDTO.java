package com.weg.quicktransfer.dto.place;

import java.util.UUID;

public record PlaceResponseDTO(
    UUID id,

    String placeName,

    String park,
    
    String section
) {
}
