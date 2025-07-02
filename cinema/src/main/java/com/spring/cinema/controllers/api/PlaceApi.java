package com.spring.cinema.controllers.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.*;


import com.spring.cinema.dto.PlaceDTO;
import com.spring.cinema.utils.Constants;

public interface PlaceApi {

    @PostMapping(value = Constants.CREATE_PLACE_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    PlaceDTO save(@RequestBody PlaceDTO dto);

    @GetMapping(value = Constants.FIND_PLACE_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    PlaceDTO getById(@PathVariable("idPlace") Long id);

    @GetMapping(value = Constants.FIND_ALL_PLACES_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<PlaceDTO> getAll();

    @DeleteMapping(value = Constants.DELETE_PLACE_ENDPOINT)
    void delete(@PathVariable("idPlace") Long id);
}