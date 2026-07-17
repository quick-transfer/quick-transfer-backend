package com.weg.quicktransfer.mapper;

import org.springframework.stereotype.Component;

import com.weg.quicktransfer.dto.user.UserResponseDTO;
import com.weg.quicktransfer.model.User;

@Component
public class UserMapper {
    public UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getUserName(),
            user.getEmail()
        );
    }
}
