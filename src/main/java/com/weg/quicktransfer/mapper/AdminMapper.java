package com.weg.quicktransfer.mapper;

import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.model.Admin;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public Admin toEntity(AdminRequestDTO adminRequestdto){
        return new Admin(
                adminRequestdto.name(),
                adminRequestdto.username(),
                adminRequestdto.email(),
                adminRequestdto.password()
        );
    }

    public AdminResponseDTO toResponse(Admin admin){
        return new AdminResponseDTO(
                admin.getId(),
                admin.getName(),
                admin.getUsername(),
                admin.getEmail()
        );
    }
}
