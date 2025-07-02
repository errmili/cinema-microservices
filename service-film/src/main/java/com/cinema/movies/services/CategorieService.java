package com.cinema.movies.services;

import java.util.List;

import com.cinema.movies.dto.CategorieDTO;

public interface CategorieService {

    CategorieDTO createCategorie(CategorieDTO categorieDTO);

    CategorieDTO getCategorieById(Long id);

    List<CategorieDTO> getAllCategories();

    void deleteCategorie(Long id);

}
