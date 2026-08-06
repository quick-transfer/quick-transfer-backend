package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.mapper.VacancyMapper;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.repo.ManagerRepository;
import com.weg.quicktransfer.repo.PlaceRepository;
import com.weg.quicktransfer.repo.VacancyRepository;
import com.weg.quicktransfer.repo.VacancySkillRepository;
import com.weg.quicktransfer.security.UserPrincipal;
import com.weg.quicktransfer.service.VacancyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyOwnershipServiceTest {
    @Mock private VacancyRepository vacancyRepository;
    @Mock private VacancyMapper vacancyMapper;
    @Mock private PlaceRepository placeRepository;
    @Mock private VacancySkillRepository vacancySkillRepository;
    @Mock private ManagerRepository managerRepository;
    @InjectMocks private VacancyService service;

    @Test
    void shouldRejectVacancyWhenPlaceHasNoSection() {
        UUID managerId = UUID.randomUUID();
        UUID placeId = UUID.randomUUID();
        Manager manager = new Manager();
        manager.setId(managerId);
        manager.setRole(Role.MANAGER);
        manager.setSection(Section.IT);
        manager.setUsername("manager");
        manager.setPassword("secret");
        manager.setActive(true);

        Place place = new Place();
        place.setId(placeId);
        place.setPark(Park.WEG_I);
        place.setSection(null);

        VacancyRequestDTO input = new VacancyRequestDTO(
                "Vaga", "Descrição", 1L, "IT", "FIRST", placeId, List.of());
        when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));
        when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));

        assertThrows(IllegalArgumentException.class, () ->
                service.create(input, new UserPrincipal(manager)));

        verify(vacancyRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verifyNoInteractions(vacancyMapper);
    }
}
