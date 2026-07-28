package com.weg.quicktransfer.exception;

import java.util.UUID;

public class InterviewNotFoundException extends ResourceNotFoundException{
    public InterviewNotFoundException(UUID id) {
        super("Interview not found with ID:" + id);
    }

    public InterviewNotFoundException(String message) {
        super(message);
    }
}
