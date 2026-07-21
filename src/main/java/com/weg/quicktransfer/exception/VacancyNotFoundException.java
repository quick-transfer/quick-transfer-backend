package com.weg.quicktransfer.exception;

public class VacancyNotFoundException extends RuntimeException{
    public VacancyNotFoundException(Long id) {
        super("Vacancy not found with ID: " + id);
    }

    public VacancyNotFoundException(String message) {
        super(message);
    }
}
