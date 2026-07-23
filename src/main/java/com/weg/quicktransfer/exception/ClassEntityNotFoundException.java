package com.weg.quicktransfer.exception;

public class ClassEntityNotFoundException extends ResourceNotFoundException{
    public ClassEntityNotFoundException(Long id) {
        super("Class not found with ID:" + id);
    }

    public ClassEntityNotFoundException(String message) {
        super(message);
    }
}
