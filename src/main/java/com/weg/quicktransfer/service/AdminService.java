package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.admin.AdminFilter;
import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.admin.AdminUpdateRequestDTO;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.AdminMapper;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.repo.AdminRepository;
import com.weg.quicktransfer.repo.specifications.AdminSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminResponseDTO saveAdmin(AdminRequestDTO adminRequestDTO) {
        if (adminRequestDTO == null) {
            throw new IllegalArgumentException("Admin request dto can not be null");
        }

        Admin admin = adminMapper.toEntity(adminRequestDTO);

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        admin.setId(UUID.randomUUID());

        adminRepository.save(admin);

        return adminMapper.toResponse(admin);
    }

    @Transactional(readOnly = true)
    public AdminResponseDTO findAdminById(UUID id) {

        return adminMapper.toResponse(adminRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User do not exists")));
    }

    @Transactional(readOnly = true)
    public List<AdminResponseDTO> findAdminByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Name can not be empty");
        }

        List<Admin> admins = adminRepository.searchAdminsByName(name);

        return admins.stream()
                .map(adminMapper::toResponse)
                .toList();
    }

    @Transactional
    public List<AdminResponseDTO> searchAdmins(AdminFilter filter) {
        Specification<Admin> spec = AdminSpecification.getFilteredAdmins(filter);
        List<Admin> admins = adminRepository.findAll(spec);

        return admins.stream()
                .map(adminMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminResponseDTO> findAllAdmin() {
        List<Admin> admins = adminRepository.findAll();

        return admins.stream()
                .map(adminMapper::toResponse)
                .toList();
    }

    @Transactional
    public AdminResponseDTO updateAdminById(UUID id, AdminUpdateRequestDTO updateRequestDTO) {

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Admin does not exist"));

        if (StringUtils.hasText(updateRequestDTO.name())) {
            admin.setName(updateRequestDTO.name());
        }

        return adminMapper.toResponse(admin);
    }

    @Transactional
    public void deleteById(UUID id) {

        if (!adminRepository.existsById(id)) {
            throw new UserNotFoundException("Admin does not exist");
        }

        adminRepository.deleteById(id);
    }
}