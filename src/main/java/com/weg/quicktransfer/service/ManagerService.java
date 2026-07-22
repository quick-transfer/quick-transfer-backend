package com.weg.quicktransfer.service;

import java.util.List;

import com.weg.quicktransfer.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.dto.manager.ManagerRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
import com.weg.quicktransfer.mapper.ManagerMapper;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.repo.ManagerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;

    private final ManagerMapper managerMapper;

    @Transactional
    public ManagerResponseDTO create(ManagerRequestDTO managerRequestDTO) {
        Manager manager = managerMapper.toEntity(managerRequestDTO);

        managerRepository.save(manager);

        return managerMapper.toResponse(manager);
    }

    @Transactional(readOnly = true)
    public List<ManagerResponseDTO> findAll() {
        List<Manager> managers = managerRepository.findAll();

        return managers.stream().map(managerMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ManagerResponseDTO findById(Long id) {
        Manager manager = managerRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return managerMapper.toResponse(manager);
    }

    @Transactional(readOnly = true)
    public ManagerResponseDTO findByName(String name) {
        Manager manager = managerRepository.findByName(name).orElseThrow(() -> new UserNotFoundException("User nof found with name: " + name));

        return managerMapper.toResponse(manager);
    }

    @Transactional
    public ManagerResponseDTO update(Long id, String name, String email, String password) {
        Manager manager = managerRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if(StringUtils.hasText(name)) {
            manager.setName(name);
        }

        if(StringUtils.hasText(email)) {
            manager.setEmail(email);
        }

        if(StringUtils.hasText(password) && password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{14,}$")) {
            manager.setPassword(password);
        }

        Manager managerAtt = managerRepository.save(manager);

        return managerMapper.toResponse(managerAtt);
    }

    @Transactional
    public void delete(Long id) {
        if(!managerRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        managerRepository.deleteById(id);
    }
}