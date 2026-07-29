package com.weg.quicktransfer.dto.place;

import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;

public record PlaceFilter(
        String placeName,
        Park park,
        Section section
) {
}
