package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.user.UserRequestDTO;
import com.weg.quicktransfer.dto.user.UserResponseDTO;
import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.mapper.AdminMapper;
import com.weg.quicktransfer.mapper.CoordinatorMapper;
import com.weg.quicktransfer.mapper.ManagerMapper;
import com.weg.quicktransfer.mapper.UserMapper;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repo.AdminRepository;
import com.weg.quicktransfer.repo.CoordinatorRepository;
import com.weg.quicktransfer.repo.ManagerRepository;
import com.weg.quicktransfer.repo.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserMapper userMapper;
    private final AdminMapper adminMapper;
    private final CoordinatorMapper coordinatorMapper;
    private final ManagerMapper managerMapper;

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final CoordinatorRepository coordinatorRepository;
    private final ManagerRepository managerRepository;

    public AdminResponseDTO saveAdmin(AdminRequestDTO adminRequestDTO) {
        Admin admin = adminMapper.toEntity(adminRequestDTO);

        adminRepository.save(admin);

        return adminMapper.toResponse(admin);
    }

    public AdminResponseDTO findAdminById(Long id) {
        return adminMapper.toResponse(adminRepository.findById(id));
    }
}
