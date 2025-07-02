package com.spring.cinema.services;

import java.util.List;

import com.spring.cinema.dto.PlaceDTO;

public interface PlaceService {

    PlaceDTO save(PlaceDTO dto);
    PlaceDTO getById(Long id);
    List<PlaceDTO> getAll();
    void delete(Long id);

    List<PlaceDTO> getPlacesByProjectionId(Long projectionId);

}

