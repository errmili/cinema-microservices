package com.cinema.movies.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.movies.models.Categorie;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {
}
