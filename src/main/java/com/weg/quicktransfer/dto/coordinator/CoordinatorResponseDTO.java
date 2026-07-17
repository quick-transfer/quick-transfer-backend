package com.weg.quicktransfer.dto.coordinator;

import java.util.List;

public record CoordinatorResponseDTO(

        String name,

        String userName,

        String email,

        String password,

        List<String> CoursesName
) {
}
