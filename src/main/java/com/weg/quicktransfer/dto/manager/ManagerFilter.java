package com.weg.quicktransfer.dto.manager;

import com.weg.quicktransfer.enums.Section;

public record ManagerFilter(
        String name,
        String username,
        Section section
) {
}
