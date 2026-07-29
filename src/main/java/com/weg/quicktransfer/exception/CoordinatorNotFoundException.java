package com.weg.quicktransfer.exception;

import java.util.UUID;

public class CoordinatorNotFoundException extends ResourceNotFoundException{
    public CoordinatorNotFoundException(UUID id) {
        super("Coordinator not found with ID:" + id);
    }

    public CoordinatorNotFoundException(String message) {
        super(message);
    }
}
