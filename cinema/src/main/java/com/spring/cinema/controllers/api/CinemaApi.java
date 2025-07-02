package com.spring.cinema.controllers.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.spring.cinema.dto.CinemaDTO;
import com.spring.cinema.utils.Constants;

public interface CinemaApi {

    @PostMapping(value = Constants.CREATE_CINEMA_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CinemaDTO save(@RequestBody CinemaDTO dto);

    @GetMapping(value = Constants.FIND_CINEMA_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    CinemaDTO getById(@PathVariable("idCinema") Long id);

    @GetMapping(value = Constants.FIND_ALL_CINEMAS_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<CinemaDTO> getAll();

    @DeleteMapping(value = Constants.DELETE_CINEMA_ENDPOINT)
    void delete(@PathVariable("idCinema") Long id);
}
