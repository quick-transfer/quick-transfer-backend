package com.weg.quicktransfer.exception;

import java.util.UUID;

public class ManagerNotFoundException extends ResourceNotFoundException{
    public ManagerNotFoundException(UUID id) {
        super("Manager not found with ID: " + id);
    }

    public ManagerNotFoundException(String message) {
        super(message);
    }
}
