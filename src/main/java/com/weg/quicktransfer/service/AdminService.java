package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.admin.AdminFilter;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.admin.AdminUpdateRequestDTO;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.AdminMapper;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.repo.AdminRepository;
import com.weg.quicktransfer.repo.specifications.AdminSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<AdminResponseDTO> searchAdmins(AdminFilter filter, Pageable pageable) {
        Specification<Admin> spec = AdminSpecification.getFilteredAdmins(filter);
        return adminRepository.findAll(spec, pageable).map(adminMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<AdminResponseDTO> findAllAdmin() {
        List<Admin> admins = adminRepository.findAll();

        return admins.stream()
                .map(adminMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<AdminResponseDTO> findAllAdmin(Pageable pageable) {
        return adminRepository.findAll(pageable).map(adminMapper::toResponse);
    }

    @Transactional
    public AdminResponseDTO updateAdminById(UUID id, AdminUpdateRequestDTO updateRequestDTO) {

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Admin does not exist"));

        if (StringUtils.hasText(updateRequestDTO.name())) {
            admin.setName(updateRequestDTO.name());
        }

        if (StringUtils.hasText(updateRequestDTO.password())) {
            admin.setPassword(passwordEncoder.encode(updateRequestDTO.password()));
            admin.setTokenVersion(admin.getTokenVersion() + 1);
        }

        return adminMapper.toResponse(adminRepository.save(admin));
    }

    @Transactional
    public void deleteById(UUID id) {

        if (!adminRepository.existsById(id)) {
            throw new UserNotFoundException("Admin does not exist");
        }

        adminRepository.deleteById(id);
    }
}
