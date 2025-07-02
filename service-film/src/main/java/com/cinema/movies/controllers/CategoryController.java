package com.cinema.movies.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cinema.movies.controllers.api.CategoryApi;
import com.cinema.movies.dto.CategorieDTO;
import com.cinema.movies.services.CategorieService;


@RestController
public class CategoryController implements CategoryApi {

    private final CategorieService categorieService;

    @Autowired
    public CategoryController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @Override
    public CategorieDTO save(@RequestBody CategorieDTO categorieDTO) {
        return categorieService.createCategorie(categorieDTO);
    }


    @Override
    public CategorieDTO findById(@PathVariable("idCategorie") Long idCategorie) {
        return categorieService.getCategorieById(idCategorie);
    }


//    @Override
//    public CategorieDTO findByCode(@PathVariable("codeCategorie") String codeCategorie) {
//        return categorieService.findByCode(codeCategorie);
//    }


    @Override
    public List<CategorieDTO> findAll() {
        return categorieService.getAllCategories();
    }

    @Override
    public void delete(@PathVariable("idCategorie") Long idCategorie) {
        categorieService.deleteCategorie(idCategorie);
    }
}
