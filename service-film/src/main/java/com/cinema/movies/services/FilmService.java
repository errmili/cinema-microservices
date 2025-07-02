package com.cinema.movies.services;

import java.util.List;
import java.util.Optional;

import com.cinema.movies.dto.FilmDTO;
import com.cinema.movies.models.Film;

public interface FilmService {

        FilmDTO createFilm(FilmDTO filmDTO);
        FilmDTO getFilmById(Long id); // Retourne FilmDTO, pas Film
        List<FilmDTO> getAllFilms();
        FilmDTO updateFilm(Long id, FilmDTO filmDetails);
        void deleteFilm(Long id);

}
