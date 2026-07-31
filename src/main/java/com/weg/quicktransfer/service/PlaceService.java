package com.weg.quicktransfer.service;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    @CacheEvict(value = "places", allEntries = true)
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
    public Page<PlaceResponseDTO> searchPlaces(PlaceFilter filter, Pageable pageable) {
        Specification<Place> spec = PlaceSpecification.getFilteredPlaces(filter);
        return placeRepository.findAll(spec, pageable).map(placeMapper::toResponse);
    }

    @Cacheable("places")
    @Transactional(readOnly = true)
    public List<PlaceResponseDTO> findAll() {
        List<Place> places = placeRepository.findAll();

        return places.stream().map(placeMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PlaceResponseDTO> findAll(Pageable pageable) {
        return placeRepository.findAll(pageable).map(placeMapper::toResponse);
    }

    @Cacheable(value = "placeById", key = "#id")
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

    @Caching(
            put = {
                    @CachePut(value = "placeById", key = "#id")
            },
            evict = {
                    @CacheEvict(value = "places", allEntries = true),
                    @CacheEvict(value = "vacancies", allEntries = true),
                    @CacheEvict(value = "vacancyById", allEntries = true)
            }
    )
    @Transactional
    public PlaceResponseDTO update(UUID id, PlaceUpdateRequestDTO placeUpdateRequestDTO) {
        Place place = placeRepository.findById(id).orElseThrow(() -> new PlaceNotFoundException(id));

        if(placeUpdateRequestDTO.placeName() != null && !placeUpdateRequestDTO.placeName().isBlank()) {
            place.setPlaceName(placeUpdateRequestDTO.placeName());
        }

        if(placeUpdateRequestDTO.park() != null && !placeUpdateRequestDTO.park().isBlank()) {
            place.setPark(Park.valueOf(placeUpdateRequestDTO.park()));
        }

        if(placeUpdateRequestDTO.section() != null && !placeUpdateRequestDTO.section().isBlank()) {
            place.setSection(Section.valueOf(placeUpdateRequestDTO.section()));
        }

        Place placeAtt = placeRepository.save(place);

        return placeMapper.toResponse(placeAtt);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "places", allEntries = true),
                    @CacheEvict(value = "placeById", key = "#id"),
                    @CacheEvict(value = "vacancies", allEntries = true),
                    @CacheEvict(value = "vacancyById", allEntries = true)
            }
    )
    @Transactional
    public void delete(UUID id) {
        if(!placeRepository.existsById(id)) {
            throw new PlaceNotFoundException(id);
        }

        placeRepository.deleteById(id);
    }
}
