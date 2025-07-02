package com.spring.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.cinema.models.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("SELECT COUNT(p) FROM Place p WHERE p.salle.id = :salleId")
    int countBySalleId(@Param("salleId") Long salleId);

//    @Query("SELECT p FROM Place p WHERE p.projectionId = :projectionId")
//    List<Place> findByProjectionId(@Param("projectionId") Long projectionId);

    @Query("SELECT p FROM Place p WHERE p.salle.id = :salleId")
    List<Place> findBySalleId(@Param("salleId") Long salleId);
}
