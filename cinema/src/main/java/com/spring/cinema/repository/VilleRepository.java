package com.spring.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.cinema.models.Ville;

public interface VilleRepository extends JpaRepository<Ville, Long> {
}
