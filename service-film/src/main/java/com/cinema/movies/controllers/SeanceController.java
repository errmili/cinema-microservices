package com.cinema.movies.controllers;


import com.cinema.movies.controllers.api.SeanceApi;
import com.cinema.movies.dto.SeanceDTO;
import com.cinema.movies.services.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SeanceController implements SeanceApi {

    private final SeanceService seanceService;

    @Autowired
    public SeanceController(SeanceService seanceService) {
        this.seanceService = seanceService;
    }

    @Override
    public SeanceDTO save(@RequestBody SeanceDTO seanceDTO) {
        return seanceService.createSeance(seanceDTO);
    }

    @Override
    public SeanceDTO findById(@PathVariable("idSeance") Long idSeance) {
        return seanceService.getSeanceById(idSeance);
    }

    @Override
    public List<SeanceDTO> findAll() {
        return seanceService.getAllSeances();
    }

    @Override
    public void delete(@PathVariable("idSeance") Long idSeance) {
        seanceService.deleteSeance(idSeance);
    }
}