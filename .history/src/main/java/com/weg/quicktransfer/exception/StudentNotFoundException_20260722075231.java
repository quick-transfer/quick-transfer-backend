package com.weg.quicktransfer.exception;

public class StudentNotFoundException extends RuntimeException{
    public StudentNotFoundException(Long id) {
        super("Student not found with ID: " + id);
    }

    public Student
}
