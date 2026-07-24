package com.weg.quicktransfer.dto.classEntity;

import com.weg.quicktransfer.enums.ShiftClass;
import com.weg.quicktransfer.enums.StatusClass;

import java.time.LocalDate;

public record ClassEntityFilter(
        String course,
        LocalDate startDate,
        LocalDate finishDate,
        StatusClass statusClass,
        ShiftClass shiftClass
) {
}
