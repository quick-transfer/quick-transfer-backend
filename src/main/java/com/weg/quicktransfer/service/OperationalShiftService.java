package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.shift.OperationalShiftResponseDTO;
import com.weg.quicktransfer.dto.shift.OperationalShiftUpdateRequestDTO;
import com.weg.quicktransfer.exception.ResourceNotFoundException;
import com.weg.quicktransfer.model.OperationalShift;
import com.weg.quicktransfer.repo.OperationalShiftRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import com.weg.quicktransfer.repo.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationalShiftService {
    private final OperationalShiftRepository shiftRepository;
    private final StudentRepository studentRepository;
    private final SystemSettingsRepository settingsRepository;

    @Transactional(readOnly = true)
    public List<OperationalShiftResponseDTO> findAll() {
        int threshold = settingsRepository.findById(1L)
                .map(settings -> settings.getHighDemandPercentage())
                .orElse(85);
        return shiftRepository.findAll(Sort.by("code")).stream()
                .map(shift -> toResponse(shift, threshold))
                .toList();
    }

    @Transactional
    public OperationalShiftResponseDTO update(UUID id, OperationalShiftUpdateRequestDTO input) {
        OperationalShift shift = shiftRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operational shift not found with ID: " + id));
        long occupancy = studentRepository.countByOperationalShiftId(id);

        if (input.name() != null && !input.name().isBlank()) shift.setName(input.name().trim());
        if (input.supervisorName() != null) {
            shift.setSupervisorName(input.supervisorName().isBlank() ? null : input.supervisorName().trim());
        }
        if (input.capacity() != null) {
            if (input.capacity() < occupancy) {
                throw new IllegalArgumentException("Capacity cannot be lower than current occupancy");
            }
            shift.setCapacity(input.capacity());
        }
        if (input.active() != null) shift.setActive(input.active());

        int threshold = settingsRepository.findById(1L)
                .map(settings -> settings.getHighDemandPercentage())
                .orElse(85);
        return toResponse(shiftRepository.save(shift), threshold);
    }

    private OperationalShiftResponseDTO toResponse(OperationalShift shift, int threshold) {
        long occupancy = studentRepository.countByOperationalShiftId(shift.getId());
        int percentage = shift.getCapacity() == 0
                ? 0
                : (int) Math.min(100, Math.round(occupancy * 100.0 / shift.getCapacity()));
        String status = occupancy >= shift.getCapacity()
                ? "FULL"
                : percentage >= threshold ? "HIGH_DEMAND" : "NORMAL";
        return new OperationalShiftResponseDTO(
                shift.getId(), shift.getName(), shift.getCode(), shift.getSupervisorName(),
                shift.getCapacity(), occupancy, percentage, status, shift.getActive());
    }
}
