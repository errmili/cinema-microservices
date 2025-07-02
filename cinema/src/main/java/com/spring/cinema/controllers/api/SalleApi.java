package com.spring.cinema.controllers.api;


import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.spring.cinema.dto.SalleDTO;
import com.spring.cinema.utils.Constants;

public interface SalleApi {

    @PostMapping(value = Constants.CREATE_SALLE_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    SalleDTO save(@RequestBody SalleDTO dto);

    @GetMapping(value = Constants.FIND_SALLE_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    SalleDTO getById(@PathVariable("idSalle") Long id);

    @GetMapping(value = Constants.FIND_ALL_SALLES_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<SalleDTO> getAll();

    @DeleteMapping(value = Constants.DELETE_SALLE_ENDPOINT)
    void delete(@PathVariable("idSalle") Long id);
}
