package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.model.Place;

public interface PlaceRepository extends JpaRepository<Place, UUID>, JpaSpecificationExecutor<Place> {
    @Query("SELECT p FROM Place p WHERE LOWER(p.placeName) LIKE LOWER(CONCAT('%', :placeName, '%'))")
    List<Place> findByPlaceName(@Param("placeName") String placeName);
}
