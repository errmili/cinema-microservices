package com.spring.cinema.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.spring.cinema.controllers.api.SalleApi;
import com.spring.cinema.dto.SalleDTO;
import com.spring.cinema.services.SalleService;

@RestController
public class SalleController implements SalleApi {
    @Autowired
    private SalleService service;

    @Override
    public SalleDTO save(SalleDTO dto) {
        return service.save(dto);
    }

    @Override
    public SalleDTO getById(Long id) {
        return service.getById(id);
    }

    @Override
    public List<SalleDTO> getAll() {
        return service.getAll();
    }

    @Override
    public void delete(Long id) {
        service.delete(id);
    }
}
