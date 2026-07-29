package com.weg.quicktransfer.service;

import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.dto.vacancy.VacancyFilter;
import com.weg.quicktransfer.repo.specifications.VacancySpecification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyResponseDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyUpdateRequestDTO;
import com.weg.quicktransfer.enums.Area;
import com.weg.quicktransfer.enums.Shift;
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.exception.VacancyNotFoundException;
import com.weg.quicktransfer.mapper.VacancyMapper;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.repo.PlaceRepository;
import com.weg.quicktransfer.repo.VacancyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final PlaceRepository placeRepository;

    @Transactional
    public VacancyResponseDTO create(VacancyRequestDTO vacancyRequestDTO) {
        Place place = placeRepository.findById(vacancyRequestDTO.placeId()).orElseThrow(() -> new PlaceNotFoundException(vacancyRequestDTO.placeId()));

        Vacancy vacancy = vacancyMapper.toEntity(vacancyRequestDTO, place);

        vacancyRepository.save(vacancy);

        return vacancyMapper.toResponse(vacancy);
    }

    @Transactional(readOnly = true)
    public List<VacancyResponseDTO> findAll() {
        List<Vacancy> vacancies = vacancyRepository.findAll();

        return vacancies.stream().map(vacancyMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public VacancyResponseDTO findById(UUID id) {
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));

        return vacancyMapper.toResponse(vacancy);
    }

    @Transactional(readOnly = true)
    public VacancyResponseDTO findByName(String name) {
        Vacancy vacancy = vacancyRepository.findFirstByName(name)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy not found with the name: " + name));

        return vacancyMapper.toResponse(vacancy);
    }

    @Transactional(readOnly = true)
    public List<VacancyResponseDTO> searchVacancies(VacancyFilter filter) {
        Specification<Vacancy> spec = VacancySpecification.getFilteredVacancies(filter);

        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        List<Vacancy> vacancies = vacancyRepository.findAll(spec, sort);

        return vacancies.stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    @Transactional
    public VacancyResponseDTO update(UUID id, VacancyUpdateRequestDTO vacancyUpdateRequestDTO) {
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));

        if(vacancyUpdateRequestDTO.name() != null && !vacancyUpdateRequestDTO.name().isBlank()) {
            vacancy.setName(vacancyUpdateRequestDTO.name());
        }

        if(vacancyUpdateRequestDTO.description() != null && !vacancyUpdateRequestDTO.description().isBlank()) {
            vacancy.setDescription(vacancyUpdateRequestDTO.description());
        }

        if(vacancyUpdateRequestDTO.numbersVacancies() != null) {
            vacancy.setNumbersVacancies(vacancyUpdateRequestDTO.numbersVacancies());
        }

        if(vacancyUpdateRequestDTO.area() != null && !vacancyUpdateRequestDTO.area().isBlank()) {
            vacancy.setArea(Area.valueOf(vacancyUpdateRequestDTO.area()));
        }

        if(vacancyUpdateRequestDTO.shift() != null && !vacancyUpdateRequestDTO.shift().isBlank()) {
            vacancy.setShift(Shift.valueOf(vacancyUpdateRequestDTO.shift()));
        }

        if(vacancyUpdateRequestDTO.placeId() != null) {
            Place place = placeRepository.findById(vacancyUpdateRequestDTO.placeId()).orElseThrow(() -> new PlaceNotFoundException(vacancyUpdateRequestDTO.placeId()));
            vacancy.setPlace(place);
        }

        Vacancy vacancyAtt = vacancyRepository.save(vacancy);

        return vacancyMapper.toResponse(vacancyAtt);
    }

    @Transactional
    public void delete(UUID id) {
        if(!vacancyRepository.existsById(id)) {
            throw new VacancyNotFoundException(id);
        }

        vacancyRepository.deleteById(id);
    }
}
