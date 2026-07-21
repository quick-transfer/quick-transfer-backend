package com.weg.quicktransfer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.weg.quicktransfer.dto.place.PlaceRequestDTO;
import com.weg.quicktransfer.dto.place.PlaceResponseDTO;
import com.weg.quicktransfer.dto.place.PlaceUpdateRequestDTO;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.mapper.PlaceMapper;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.repo.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    public PlaceResponseDTO create(PlaceRequestDTO placeRequestDTO) {
        Place place = placeMapper.toEntity(placeRequestDTO);

        placeRepository.save(place);

        return placeMapper.toResponse(place);
    }

    public List<PlaceResponseDTO> findAll() {
        List<Place> places = placeRepository.findAll();

        return places.stream().map(placeMapper::toResponse).toList();
    }

    public PlaceResponseDTO findById(Long id) {
        Place place = placeRepository.findById(id).orElseThrow(() -> new PlaceNotFoundException(id));

        return placeMapper.toResponse(place);
    }

    public PlaceResponseDTO update(Long id, PlaceUpdateRequestDTO placeUpdateRequestDTO) {
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

    public void delete(Long id) {
        if(!placeRepository.existsById(id)) {
            throw new PlaceNotFoundException(id);
        }

        placeRepository.deleteById(id);
    }
}
