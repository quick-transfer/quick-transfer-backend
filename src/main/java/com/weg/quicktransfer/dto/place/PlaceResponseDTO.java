package com.weg.quicktransfer.dto.place;

public record PlaceResponseDTO(
    Long id,
    String placeName,
    String park,
    String section
) {
}
