package com.cinema.movies.controllers;

import com.cinema.movies.controllers.api.FilmApi;
import com.cinema.movies.dto.FilmDTO;
import com.cinema.movies.services.FilmService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class FilmController implements FilmApi {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    public FilmDTO save(@RequestBody FilmDTO filmDTO) {
        return filmService.createFilm(filmDTO);
    }

    @Override
    public FilmDTO findById(@PathVariable("idFilm") Long idFilm) {
        return filmService.getFilmById(idFilm);
    }

    @Override
    public List<FilmDTO> findAll() {
        return filmService.getAllFilms();
    }

    @Override
    public FilmDTO update(@PathVariable("idFilm") Long idFilm, @RequestBody FilmDTO filmDTO) {
        return filmService.updateFilm(idFilm, filmDTO);
    }

    @Override
    public void delete(@PathVariable("idFilm") Long idFilm) {
        filmService.deleteFilm(idFilm);
    }
}