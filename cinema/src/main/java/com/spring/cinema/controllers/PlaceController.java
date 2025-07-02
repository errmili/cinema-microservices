package com.spring.cinema.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.cinema.controllers.api.PlaceApi;
import com.spring.cinema.dto.PlaceDTO;
import com.spring.cinema.services.PlaceService;


@RestController
public class PlaceController implements PlaceApi {
    @Autowired
    private PlaceService service;

    @Override
    public PlaceDTO save(PlaceDTO dto) {
        return service.save(dto);
    }

    @Override
    public PlaceDTO getById(Long id) {
        return service.getById(id);
    }

    @Override
    public List<PlaceDTO> getAll() {
        return service.getAll();
    }

    @Override
    public void delete(Long id) {
        service.delete(id);
    }

    @GetMapping("cinema/v1/places/projection/{projectionId}")
    public ResponseEntity<List<PlaceDTO>> getPlacesByProjection(@PathVariable Long projectionId) {
        List<PlaceDTO> places = service.getPlacesByProjectionId(projectionId);
        return ResponseEntity.ok(places);
    }
}

