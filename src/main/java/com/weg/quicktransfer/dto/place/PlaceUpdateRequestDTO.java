package com.weg.quicktransfer.dto.place;

public record PlaceUpdateRequestDTO(
    String placeName,

    String park,

    String section,

    String code,

    String description,

    String city,

    String state,

    String status
) {
    public PlaceUpdateRequestDTO(String placeName, String park, String section) {
        this(placeName, park, section, null, null, null, null, null);
    }
}
