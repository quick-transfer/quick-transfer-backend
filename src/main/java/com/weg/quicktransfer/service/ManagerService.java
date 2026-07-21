package com.weg.quicktransfer.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.dto.manager.ManagerRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
import com.weg.quicktransfer.dto.manager.ManagerUpdateRequestDTO;
import com.weg.quicktransfer.exception.ManagerNotFoundException;
import com.weg.quicktransfer.mapper.ManagerMapper;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.repo.ManagerRepository;

import lombok.RequiredArgsConstructor;

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
        Manager manager = managerRepository.findById(id).orElseThrow(() -> new ManagerNotFoundException(id));

        return managerMapper.toResponse(manager);
    }

    @Transactional
    public ManagerResponseDTO update(Long id, ManagerUpdateRequestDTO managerUpdateRequestDTO) {
        Manager manager = managerRepository.findById(id).orElseThrow(() -> new ManagerNotFoundException(id));

        if(managerUpdateRequestDTO.name() != null && !managerUpdateRequestDTO.name().isBlank()) {
            manager.setName(managerUpdateRequestDTO.name());
        }

        if(managerUpdateRequestDTO.username() != null && !managerUpdateRequestDTO.username().isBlank()) {
            manager.setUsername(managerUpdateRequestDTO.username());
        }

        if(managerUpdateRequestDTO.email() != null && !managerUpdateRequestDTO.email().isBlank()) {
            manager.setEmail(managerUpdateRequestDTO.email());
        }

        if(managerUpdateRequestDTO.password() != null && !managerUpdateRequestDTO.password().isBlank()) {
            manager.setPassword(managerUpdateRequestDTO.password());
        }

        Manager managerAtt = managerRepository.save(manager);

        return managerMapper.toResponse(managerAtt);
    }

    @Transactional
    public void delete(Long id) {
        if(!managerRepository.existsById(id)) {
            throw new ManagerNotFoundException(id);
        }

        managerRepository.deleteById(id);
    }
}