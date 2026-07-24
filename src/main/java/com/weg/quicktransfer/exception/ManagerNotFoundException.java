package com.weg.quicktransfer.exception;

public class ManagerNotFoundException extends ResourceNotFoundException{
    public ManagerNotFoundException(Long id) {
        super("Manager not found with ID: " + id);
    }

    public ManagerNotFoundException(String message) {
        super(message);
    }
}
