package com.weg.quicktransfer.exception;

import java.util.UUID;

public class PlaceNotFoundException extends ResourceNotFoundException{
    public PlaceNotFoundException(UUID id) {
        super("Place not found with ID: " + id);
    }

    public PlaceNotFoundException(String message) {
        super(message);
    }
}
