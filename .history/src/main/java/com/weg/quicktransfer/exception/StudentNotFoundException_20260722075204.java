package com.weg.quicktransfer.exception;

public class StudentNotFoundException extends RuntimeException{
    public StudentNotFoundException(Long id) {
        super("Place not found with ID: " + id);
    }
}
