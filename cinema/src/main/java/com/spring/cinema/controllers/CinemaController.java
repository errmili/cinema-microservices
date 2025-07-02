package com.spring.cinema.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.cinema.controllers.api.CinemaApi;
import com.spring.cinema.dto.CinemaDTO;
import com.spring.cinema.services.CinemaService;
import com.spring.cinema.services.impl.CinemaServiceImpl;

@RestController
public class CinemaController implements CinemaApi {
    @Autowired
    private CinemaServiceImpl service;

    @Override
    public CinemaDTO save(CinemaDTO dto) {
        return service.save(dto);
    }

    @Override
    public CinemaDTO getById(Long id) {
        return service.getById(id);
    }

    @Override
    public List<CinemaDTO> getAll() {
        return service.getAll();
    }

    @Override
    public void delete(Long id) {
        service.delete(id);
    }


    @GetMapping("/{id}/nombreSalles")
    public ResponseEntity<Integer> getNombreSalles(@PathVariable Long id) {
        int nombre = service.getNombreSalles(id);
        return ResponseEntity.ok(nombre);
    }

}