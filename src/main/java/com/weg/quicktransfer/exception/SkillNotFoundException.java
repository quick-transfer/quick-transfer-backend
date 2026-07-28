package com.weg.quicktransfer.exception;

import java.util.UUID;

public class SkillNotFoundException extends ResourceNotFoundException{
    public SkillNotFoundException(UUID id) {
        super("Skill not found with ID: " + id);
    }

    public SkillNotFoundException(String message) {
        super(message);
    }
}
