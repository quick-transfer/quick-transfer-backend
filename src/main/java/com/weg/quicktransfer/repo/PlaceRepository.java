package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.Place;

import java.util.UUID;

public interface PlaceRepository extends JpaRepository<Place, UUID> {
    
}
