package com.weg.quicktransfer.exception;

import java.util.UUID;

public class StudentNotFoundException extends ResourceNotFoundException{
    public StudentNotFoundException(UUID id) {
        super("Student not found with ID: " + id);
    }

    public StudentNotFoundException(String message) {
        super(message);
    }
}
