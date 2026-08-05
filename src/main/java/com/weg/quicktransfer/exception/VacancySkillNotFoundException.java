package com.weg.quicktransfer.exception;

import java.util.UUID;

public class VacancySkillNotFoundException extends ResourceNotFoundException {

    public VacancySkillNotFoundException(UUID id) {
        super("Vacancy skill not found with ID: " + id);
    }

    public VacancySkillNotFoundException(String message) {
        super(message);
    }
}
