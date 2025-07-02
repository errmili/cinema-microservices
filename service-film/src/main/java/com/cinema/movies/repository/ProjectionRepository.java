package com.cinema.movies.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.movies.models.Projection;

public interface ProjectionRepository extends JpaRepository<Projection, Long> {
}
