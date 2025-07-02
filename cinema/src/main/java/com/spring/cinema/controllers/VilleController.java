package com.spring.cinema.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.spring.cinema.controllers.api.VilleApi;
import com.spring.cinema.dto.VilleDTO;

import com.spring.cinema.services.VilleService;

@RestController
public class VilleController implements VilleApi {

    @Autowired
    private VilleService service;

    @Override
    public VilleDTO save(VilleDTO dto) {
        return service.save(dto);
    }

    @Override
    public VilleDTO getById(Long id) {
        return service.getById(id);
    }

    @Override
    public List<VilleDTO> getAll() {
        return service.getAll();
    }

    @Override
    public void delete(Long id) {
        service.delete(id);
    }
}
