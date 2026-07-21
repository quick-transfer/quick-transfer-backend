package com.weg.quicktransfer.dto.coordinator;

import java.util.List;

public record CoordinatorResponseDTO(
        String name,

        String username,

        String email,

        List<String> coursesName
) {
}
