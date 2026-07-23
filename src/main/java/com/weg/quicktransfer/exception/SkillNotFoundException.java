package com.weg.quicktransfer.exception;

public class SkillNotFoundException extends ResourceNotFoundException{
    public SkillNotFoundException(Long id) {
        super("Skill not found with ID: " + id);
    }

    public SkillNotFoundException(String message) {
        super(message);
    }
}
