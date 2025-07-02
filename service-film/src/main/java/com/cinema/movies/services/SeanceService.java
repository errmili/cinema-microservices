package com.cinema.movies.services;

import java.util.List;

import com.cinema.movies.dto.SeanceDTO;

public interface SeanceService {

    SeanceDTO createSeance(SeanceDTO seanceDTO);
    SeanceDTO getSeanceById(Long id);
    void deleteSeance(Long id);
    List<SeanceDTO> getAllSeances();

}
