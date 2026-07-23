package com.weg.quicktransfer.exception;

public class InterviewNotFoundException extends ResourceNotFoundException{
    public InterviewNotFoundException(Long id) {
        super("Interview not found with ID:" + id);
    }

    public InterviewNotFoundException(String message) {
        super(message);
    }
}
