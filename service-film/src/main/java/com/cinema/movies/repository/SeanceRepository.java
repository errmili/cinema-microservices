package com.cinema.movies.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.movies.models.Seance;

public interface SeanceRepository extends JpaRepository<Seance, Long> {
}
