package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.AdminMapper;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.repo.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    public AdminResponseDTO saveAdmin(AdminRequestDTO adminRequestDTO) {
        Admin admin = adminMapper.toEntity(adminRequestDTO);

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        adminRepository.save(admin);

        return adminMapper.toResponse(admin);
    }

    public AdminResponseDTO findAdminById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id can not be less than 1");
        }

        return adminMapper.toResponse(adminRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User do not exists")));
    }

    public AdminResponseDTO findAdminByName(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id can not be less than 0");
        }

        return adminMapper.toResponse(adminRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User do not exists")));
    }
}
