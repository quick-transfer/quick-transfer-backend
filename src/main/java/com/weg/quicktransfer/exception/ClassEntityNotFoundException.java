package com.weg.quicktransfer.exception;

import java.util.UUID;

public class ClassEntityNotFoundException extends ResourceNotFoundException{
    public ClassEntityNotFoundException(UUID id) {
        super("Class not found with ID:" + id);
    }

    public ClassEntityNotFoundException(String message) {
        super(message);
    }
}
