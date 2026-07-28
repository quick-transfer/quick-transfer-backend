package com.weg.quicktransfer.exception;

import java.util.UUID;

public class CourseNotFoundException extends ResourceNotFoundException {
    public CourseNotFoundException(UUID id) {
        super("Course not found with ID:" + id);
    }

    public CourseNotFoundException(String message) {
        super(message);
    }
}
