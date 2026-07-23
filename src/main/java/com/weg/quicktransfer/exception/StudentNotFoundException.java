package com.weg.quicktransfer.exception;

public class StudentNotFoundException extends ResourceNotFoundException{
    public StudentNotFoundException(Long id) {
        super("Student not found with ID: " + id);
    }

    public StudentNotFoundException(String message) {
        super(message);
    }
}
