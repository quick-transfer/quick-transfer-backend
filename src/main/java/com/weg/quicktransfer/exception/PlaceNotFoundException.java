package com.weg.quicktransfer.exception;

public class PlaceNotFoundException extends RuntimeException{
    public PlaceNotFoundException(Long id) {
        super("Place not found with ID: " + id);
    }

    public PlaceNotFoundException(String message) {
        super(message);
    }
}
