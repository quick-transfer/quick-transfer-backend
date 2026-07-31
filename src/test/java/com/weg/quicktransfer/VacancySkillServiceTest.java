package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.vacancy.VacancySkillFilter;
import com.weg.quicktransfer.dto.vacancy.VacancySkillRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancySkillResponseDTO;
import com.weg.quicktransfer.dto.vacancy.VacancySkillUpdateRequestDTO;
import com.weg.quicktransfer.enums.SkillType;
import com.weg.quicktransfer.exception.VacancySkillNotFoundException;
import com.weg.quicktransfer.mapper.VacancySkillMapper;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.model.VacancySkill;
import com.weg.quicktransfer.repo.VacancyRepository;
import com.weg.quicktransfer.repo.VacancySkillRepository;
import com.weg.quicktransfer.service.VacancySkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancySkillServiceTest {

    private static final UUID SKILL_ID =
            UUID.fromString("123e4567-e89b-12d3-a456-426614174555");
    private static final UUID NON_EXISTENT_ID =
            UUID.fromString("123e4567-e89b-12d3-a456-426614174999");

    @Mock
    private VacancySkillRepository vacancySkillRepository;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private VacancySkillMapper vacancySkillMapper;

    @InjectMocks
    private VacancySkillService vacancySkillService;

    private VacancySkill vacancySkill;
    private VacancySkillRequestDTO requestDTO;
    private VacancySkillResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        vacancySkill = new VacancySkill("Java", SkillType.TECHNICAL, 7.0);
        vacancySkill.setId(SKILL_ID);
        requestDTO = new VacancySkillRequestDTO("Java", SkillType.TECHNICAL.name(), 7.0);
        responseDTO = new VacancySkillResponseDTO(
                SKILL_ID, "Java", SkillType.TECHNICAL.name(), 7.0);
    }

    @Test
    void shouldCreateVacancySkill() {
        when(vacancySkillMapper.toEntity(requestDTO)).thenReturn(vacancySkill);
        when(vacancySkillRepository.save(vacancySkill)).thenReturn(vacancySkill);
        when(vacancySkillMapper.toResponse(vacancySkill)).thenReturn(responseDTO);

        VacancySkillResponseDTO result = vacancySkillService.create(requestDTO);

        assertEquals(responseDTO, result);
        verify(vacancySkillRepository).save(vacancySkill);
    }

    @Test
    void shouldFindVacancySkillById() {
        when(vacancySkillRepository.findById(SKILL_ID)).thenReturn(Optional.of(vacancySkill));
        when(vacancySkillMapper.toResponse(vacancySkill)).thenReturn(responseDTO);

        assertEquals(responseDTO, vacancySkillService.findById(SKILL_ID));
    }

    @Test
    void shouldThrowWhenVacancySkillIsNotFoundById() {
        when(vacancySkillRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        assertThrows(
                VacancySkillNotFoundException.class,
                () -> vacancySkillService.findById(NON_EXISTENT_ID));
    }

    @Test
    void shouldFindAllVacancySkillsSortedByName() {
        when(vacancySkillRepository.findAll(ArgumentMatchers.any(Sort.class)))
                .thenReturn(List.of(vacancySkill));
        when(vacancySkillMapper.toResponse(vacancySkill)).thenReturn(responseDTO);

        List<VacancySkillResponseDTO> result = vacancySkillService.findAll();

        assertEquals(List.of(responseDTO), result);
        verify(vacancySkillRepository).findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Test
    void shouldSearchVacancySkills() {
        VacancySkillFilter filter = new VacancySkillFilter("java", SkillType.TECHNICAL, 7.0);
        when(vacancySkillRepository.findAll(
                ArgumentMatchers.<Specification<VacancySkill>>any(),
                ArgumentMatchers.any(Sort.class)))
                .thenReturn(List.of(vacancySkill));
        when(vacancySkillMapper.toResponse(vacancySkill)).thenReturn(responseDTO);

        List<VacancySkillResponseDTO> result = vacancySkillService.searchSkills(filter);

        assertEquals(List.of(responseDTO), result);
    }

    @Test
    void shouldUpdateVacancySkill() {
        VacancySkillUpdateRequestDTO updateRequest =
                new VacancySkillUpdateRequestDTO("Kotlin", "SOCIOEMOTIONAL", 8.5);
        VacancySkillResponseDTO updatedResponse = new VacancySkillResponseDTO(
                SKILL_ID, "Kotlin", SkillType.SOCIOEMOTIONAL.name(), 8.5);
        when(vacancySkillRepository.findById(SKILL_ID)).thenReturn(Optional.of(vacancySkill));
        when(vacancySkillRepository.save(vacancySkill)).thenReturn(vacancySkill);
        when(vacancySkillMapper.toResponse(vacancySkill)).thenReturn(updatedResponse);

        VacancySkillResponseDTO result = vacancySkillService.update(SKILL_ID, updateRequest);

        assertEquals(updatedResponse, result);
        assertEquals("Kotlin", vacancySkill.getName());
        assertEquals(SkillType.SOCIOEMOTIONAL, vacancySkill.getSkillType());
        assertEquals(8.5, vacancySkill.getMinimumGrade());
    }

    @Test
    void shouldDeleteVacancySkillAndRemoveVacancyAssociations() {
        Vacancy vacancy = new Vacancy();
        vacancy.addSkill(vacancySkill);
        when(vacancySkillRepository.findById(SKILL_ID)).thenReturn(Optional.of(vacancySkill));

        vacancySkillService.delete(SKILL_ID);

        assertFalse(vacancy.getSkills().contains(vacancySkill));
        assertFalse(vacancySkill.getVacancies().contains(vacancy));
        verify(vacancyRepository).saveAll(List.of(vacancy));
        verify(vacancySkillRepository).delete(vacancySkill);
    }

    @Test
    void shouldNotDeleteMissingVacancySkill() {
        when(vacancySkillRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        assertThrows(
                VacancySkillNotFoundException.class,
                () -> vacancySkillService.delete(NON_EXISTENT_ID));
        verify(vacancySkillRepository, never()).delete(vacancySkill);
    }
}
