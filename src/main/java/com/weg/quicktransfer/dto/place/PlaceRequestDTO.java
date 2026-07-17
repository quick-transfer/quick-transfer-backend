package com.weg.quicktransfer.dto.place;

import jakarta.validation.constraints.NotBlank;

public record PlaceRequestDTO(
    @NotBlank(message = "Park must not be blank")
    String park,
    @NotBlank(message = "Section must not be blank")
    String section
) {
}
