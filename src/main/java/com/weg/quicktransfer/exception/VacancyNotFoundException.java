package com.weg.quicktransfer.exception;

import java.util.UUID;

public class VacancyNotFoundException extends ResourceNotFoundException{
    public VacancyNotFoundException(UUID id) {
        super("Vacancy not found with ID: " + id);
    }

    public VacancyNotFoundException(String message) {
        super(message);
    }
}
