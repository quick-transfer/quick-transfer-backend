package com.weg.quicktransfer.exception;

public class CoordinatorNotFoundException extends RuntimeException{
    public CoordinatorNotFoundException(Long id) {
        super("Coordinator not found with ID:" + id);
    }

    public CoordinatorNotFoundException(String message) {
        super(message);
    }
}
