package com.spring.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.cinema.models.Cinema;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
}
