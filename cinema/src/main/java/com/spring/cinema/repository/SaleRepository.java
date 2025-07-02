package com.spring.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.cinema.models.Salle;

public interface SaleRepository extends JpaRepository<Salle, Long> {

    @Query("SELECT COUNT(s) FROM Salle s WHERE s.cinema.id = :cinemaId")
    int countByCinemaId(@Param("cinemaId") Long cinemaId);

}
