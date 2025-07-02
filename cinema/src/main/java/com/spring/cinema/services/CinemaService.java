package com.spring.cinema.services;

import java.util.List;

import com.spring.cinema.dto.CinemaDTO;

public interface CinemaService {

    CinemaDTO save(CinemaDTO dto);
    CinemaDTO getById(Long id);
    List<CinemaDTO> getAll();
    void delete(Long id);
}
