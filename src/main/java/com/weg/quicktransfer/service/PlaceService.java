package com.weg.quicktransfer.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.dto.place.PlaceFilter;
import com.weg.quicktransfer.dto.place.PlaceRequestDTO;
import com.weg.quicktransfer.dto.place.PlaceResponseDTO;
import com.weg.quicktransfer.dto.place.PlaceUpdateRequestDTO;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.mapper.PlaceMapper;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.repo.PlaceRepository;
import com.weg.quicktransfer.repo.specifications.PlaceSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    @Transactional
    public PlaceResponseDTO create(PlaceRequestDTO placeRequestDTO) {
        if (placeRequestDTO == null) {
            throw new IllegalArgumentException("Place request can not be null");
        }

        Place place = placeMapper.toEntity(placeRequestDTO);

        place = placeRepository.save(place);

        return placeMapper.toResponse(place);
    }

    @Transactional(readOnly = true)
    public List<PlaceResponseDTO> searchPlaces(PlaceFilter filter) {
        Specification<Place> spec = PlaceSpecification.getFilteredPlaces(filter);

        List<Place> places = placeRepository.findAll(spec);

        return places.stream()
                .map(placeMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PlaceResponseDTO> findAll() {
        List<Place> places = placeRepository.findAll();

        return places.stream().map(placeMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PlaceResponseDTO findById(UUID id) {
        Place place = placeRepository.findById(id).orElseThrow(() -> new PlaceNotFoundException(id));

        return placeMapper.toResponse(place);
    }

    @Transactional(readOnly = true)
    public List<PlaceResponseDTO> findByName(String placeName) {
        List<Place> places = placeRepository.findByPlaceName(placeName);

        return places.stream().map(placeMapper::toResponse).toList();
    }

    @Transactional
    public PlaceResponseDTO update(UUID id, PlaceUpdateRequestDTO placeUpdateRequestDTO) {
        Place place = placeRepository.findById(id).orElseThrow(() -> new PlaceNotFoundException(id));

        if(placeUpdateRequestDTO.placeName() != null && !placeUpdateRequestDTO.placeName().isBlank()) {
            place.setPlaceName(placeUpdateRequestDTO.placeName());
        }

        if(placeUpdateRequestDTO.park() != null && !placeUpdateRequestDTO.park().isBlank()) {
            place.setPark(Park.valueOf(placeUpdateRequestDTO.park().trim().toUpperCase(Locale.ROOT)));
        }

        if(placeUpdateRequestDTO.section() != null && !placeUpdateRequestDTO.section().isBlank()) {
            place.setSection(Section.valueOf(placeUpdateRequestDTO.section().trim().toUpperCase(Locale.ROOT)));
        }

        Place placeAtt = placeRepository.save(place);

        return placeMapper.toResponse(placeAtt);
    }

    @Transactional
    public void delete(UUID id) {
        if(!placeRepository.existsById(id)) {
            throw new PlaceNotFoundException(id);
        }

        placeRepository.deleteById(id);
    }
}
