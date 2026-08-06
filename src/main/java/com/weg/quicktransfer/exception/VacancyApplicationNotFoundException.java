package com.weg.quicktransfer.exception;

import java.util.UUID;

public class VacancyApplicationNotFoundException extends ResourceNotFoundException {
    public VacancyApplicationNotFoundException(UUID id) {
        super("Vacancy application not found with ID: " + id);
    }
}
