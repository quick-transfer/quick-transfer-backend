package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.coordinator.CoordinatorFilter;
import com.weg.quicktransfer.dto.coordinator.CoordinatorRequestDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorResponseDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorUpdateRequestDTO;
import com.weg.quicktransfer.exception.CoordinatorNotFoundException;
import com.weg.quicktransfer.exception.UserNotAllowdException;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.CoordinatorMapper;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repo.CoordinatorRepository;

import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.repo.specifications.CoordinatorSpecification;
import org.springframework.data.jpa.domain.Specification;
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

    private final UserRepository userRepository;

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

    @Transactional
    public List<CoordinatorResponseDTO> searchCoordinators(CoordinatorFilter filter) {
        Specification<Coordinator> spec = CoordinatorSpecification.getFilteredCoordinators(filter);

        List<Coordinator> coordinators = coordinatorRepository.findAll(spec);

        return coordinators.stream()
                .map(coordinatorMapper::toResponse)
                .toList();
    }

    @Transactional
    public CoordinatorResponseDTO update(UUID id, CoordinatorUpdateRequestDTO coordinatorUpdateRequestDTO, String requesterUsername) {
        User user = userRepository.findFirstByUsername(requesterUsername)
                .orElseThrow(() -> new UserNotFoundException("User is not logged"));

        if (!(user instanceof Coordinator || user instanceof Admin)) {
            throw new UserNotAllowdException("User is neither a Admin nor a Coordinator");
        }

        if (user instanceof Admin || id.equals(user.getId())) {
            Coordinator coordinator = coordinatorRepository.findById(id).orElseThrow(() -> new CoordinatorNotFoundException(id));

            if(coordinatorUpdateRequestDTO.name() != null && !coordinatorUpdateRequestDTO.name().isBlank()) {
                coordinator.setName(coordinatorUpdateRequestDTO.name());
            }

            if(coordinatorUpdateRequestDTO.password() != null && !coordinatorUpdateRequestDTO.password().isBlank()) {
                coordinator.setPassword(passwordEncoder.encode(coordinatorUpdateRequestDTO.password()));
                coordinator.setTokenVersion(coordinator.getTokenVersion() + 1);
            }

            Coordinator coordinatorAtt = coordinatorRepository.save(coordinator);

            return coordinatorMapper.toResponse(coordinatorAtt);
        } else {
            throw new UserNotAllowdException("User is not the given coordinator");
        }
    }

    @Transactional
    public void delete(UUID id) {
        if(!coordinatorRepository.existsById(id)) {
            throw new CoordinatorNotFoundException(id);
        }

        coordinatorRepository.deleteById(id);
    }
}
