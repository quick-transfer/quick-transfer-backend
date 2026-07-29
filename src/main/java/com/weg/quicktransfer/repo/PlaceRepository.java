package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import com.weg.quicktransfer.model.Place;

public interface PlaceRepository extends JpaRepository<Place, UUID>, JpaSpecificationExecutor<Place> {
    
}
