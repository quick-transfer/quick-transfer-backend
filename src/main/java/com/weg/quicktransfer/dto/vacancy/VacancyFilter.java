package com.weg.quicktransfer.dto.vacancy;

import com.weg.quicktransfer.enums.Area;
import com.weg.quicktransfer.enums.Shift;

public record VacancyFilter(
        String name,

        String description,

        Long numberVacancies,

        Area area,

        Shift shift,

        String placeName
) {
}
