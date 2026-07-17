package com.weg.quicktransfer.dto.place;

import jakarta.validation.constraints.NotBlank;

public record PlaceRequestDTO(
    @NotBlank
    String park,
    @NotBlank
    String section
) {
}
