package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.coordinator.CoordinatorFilter;
import com.weg.quicktransfer.dto.coordinator.CoordinatorRequestDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorResponseDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorUpdateRequestDTO;
import com.weg.quicktransfer.exception.CoordinatorNotFoundException;
import com.weg.quicktransfer.mapper.CoordinatorMapper;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.repo.CoordinatorRepository;

import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.repo.specifications.CoordinatorSpecification;
import com.weg.quicktransfer.security.PasswordPolicy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoordinatorService {
    private final PasswordEncoder passwordEncoder;
    private final CoordinatorRepository coordinatorRepository;
    private final CoordinatorMapper coordinatorMapper;

    @Transactional
    public CoordinatorResponseDTO create(CoordinatorRequestDTO coordinatorRequestDTO) {
        Coordinator coordinator = coordinatorMapper.toEntity(coordinatorRequestDTO);

        coordinator.setPassword(passwordEncoder.encode(coordinator.getPassword()));

        coordinator = coordinatorRepository.save(coordinator);

        return coordinatorMapper.toResponse(coordinator);
    }

    @Transactional(readOnly = true)
    public List<CoordinatorResponseDTO> findAll() {
        List<Coordinator> coordinators = coordinatorRepository.findAll();

        return coordinators.stream().map(coordinatorMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<CoordinatorResponseDTO> findAll(Pageable pageable) {
        return coordinatorRepository.findAll(pageable).map(coordinatorMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CoordinatorResponseDTO findById(UUID id){
        Coordinator coordinator = coordinatorRepository.findById(id).orElseThrow(() -> new CoordinatorNotFoundException(id));

        return coordinatorMapper.toResponse(coordinator);
    }

    @Transactional(readOnly = true)
    public List<CoordinatorResponseDTO> findByName(String name){
        List<Coordinator> coordinators = coordinatorRepository.searchUsersByName(name);

        return coordinators.stream()
                .map(coordinatorMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CoordinatorResponseDTO> searchCoordinators(CoordinatorFilter filter, Pageable pageable) {
        Specification<Coordinator> spec = CoordinatorSpecification.getFilteredCoordinators(filter);
        return coordinatorRepository.findAll(spec, pageable).map(coordinatorMapper::toResponse);
    }

    @Transactional
    public List<CoordinatorResponseDTO> searchCoordinators(CoordinatorFilter filter) {
        Specification<Coordinator> spec = CoordinatorSpecification.getFilteredCoordinators(filter);

        List<Coordinator> coordinators = coordinatorRepository.findAll(spec);

        return coordinators.stream()
                .map(coordinatorMapper::toResponse)
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "courses", allEntries = true),
            @CacheEvict(value = "courseById", allEntries = true)
    })
    public CoordinatorResponseDTO update(UUID id, CoordinatorUpdateRequestDTO coordinatorUpdateRequestDTO) {
        Coordinator coordinator = coordinatorRepository.findById(id)
                .orElseThrow(() -> new CoordinatorNotFoundException(id));

        if (coordinatorUpdateRequestDTO.name() != null && !coordinatorUpdateRequestDTO.name().isBlank()) {
            coordinator.setName(coordinatorUpdateRequestDTO.name());
        }

        if (coordinatorUpdateRequestDTO.password() != null && !coordinatorUpdateRequestDTO.password().isBlank()) {
            PasswordPolicy.validate(coordinatorUpdateRequestDTO.password());
            coordinator.setPassword(passwordEncoder.encode(coordinatorUpdateRequestDTO.password()));
        }

        Coordinator coordinatorAtt = coordinatorRepository.save(coordinator);
        return coordinatorMapper.toResponse(coordinatorAtt);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "courses", allEntries = true),
            @CacheEvict(value = "courseById", allEntries = true)
    })
    public void delete(UUID id) {
        if(!coordinatorRepository.existsById(id)) {
            throw new CoordinatorNotFoundException(id);
        }

        coordinatorRepository.deleteById(id);
    }
}
