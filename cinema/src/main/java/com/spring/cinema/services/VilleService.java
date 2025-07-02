package com.spring.cinema.services;

import java.util.List;

import com.spring.cinema.dto.VilleDTO;

public interface VilleService {

    VilleDTO save(VilleDTO dto);
    VilleDTO getById(Long id);
    List<VilleDTO> getAll();
    void delete(Long id);
}
