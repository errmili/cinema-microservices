package com.spring.cinema.services;

import java.util.List;

import com.spring.cinema.dto.SalleDTO;

public interface SalleService {

    SalleDTO save(SalleDTO dto);
    SalleDTO getById(Long id);
    List<SalleDTO> getAll();
    void delete(Long id);
}
