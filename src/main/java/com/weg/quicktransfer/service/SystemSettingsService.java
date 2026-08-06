package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.settings.SystemSettingsResponseDTO;
import com.weg.quicktransfer.dto.settings.SystemSettingsUpdateRequestDTO;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.model.SystemSettings;
import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repo.SystemSettingsRepository;
import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemSettingsService {
    private final SystemSettingsRepository settingsRepository;
    private final UserRepository userRepository;

    @Transactional
    public SystemSettingsResponseDTO find() {
        return toResponse(findOrCreate());
    }

    @Transactional
    public SystemSettingsResponseDTO update(
            SystemSettingsUpdateRequestDTO input,
            UserPrincipal principal) {
        SystemSettings settings = findOrCreate();
        if (input.defaultShiftCapacity() != null) {
            settings.setDefaultShiftCapacity(input.defaultShiftCapacity());
        }
        if (input.highDemandPercentage() != null) {
            settings.setHighDemandPercentage(input.highDemandPercentage());
        }
        if (input.emailSender() != null && !input.emailSender().isBlank()) {
            settings.setEmailSender(input.emailSender().trim());
        }
        User actor = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(principal.getId()));
        settings.setUpdatedBy(actor);
        return toResponse(settingsRepository.save(settings));
    }

    private SystemSettings findOrCreate() {
        return settingsRepository.findById(1L).orElseGet(() -> {
            SystemSettings settings = new SystemSettings();
            settings.setId(1L);
            settings.setDefaultShiftCapacity(50);
            settings.setHighDemandPercentage(85);
            settings.setEmailSender("notificacoes.quicktransfer@weg.net");
            return settingsRepository.save(settings);
        });
    }

    private SystemSettingsResponseDTO toResponse(SystemSettings settings) {
        return new SystemSettingsResponseDTO(
                settings.getDefaultShiftCapacity(),
                settings.getHighDemandPercentage(),
                settings.getEmailSender(),
                settings.getUpdatedAt(),
                settings.getUpdatedBy() == null ? null : settings.getUpdatedBy().getName());
    }
}
