package com.cinema.movies.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.movies.models.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {
}
