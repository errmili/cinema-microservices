package com.cinema.movies.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.movies.dto.CategorieDTO;
import com.cinema.movies.dto.FilmDTO;
import com.cinema.movies.exception.BusinessException;
import com.cinema.movies.exception.EntityCreationException;
import com.cinema.movies.exception.InvalidEntityException;
import com.cinema.movies.handler.BusinessErrorCodes;
import com.cinema.movies.models.Categorie;
import com.cinema.movies.models.Film;
import com.cinema.movies.repository.CategorieRepository;
import com.cinema.movies.repository.FilmRepository;
import com.cinema.movies.services.CategorieService;
import com.cinema.movies.services.FilmService;
import com.cinema.movies.validators.ObjectsValidator;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmRepository filmRepository;
    private final ObjectsValidator<FilmDTO> objectsValidator;

    private final CategorieService categorieService;

    @Autowired
    public FilmServiceImpl(FilmRepository filmRepository, ObjectsValidator<FilmDTO> objectsValidator, CategorieService categorieService) {
        this.filmRepository = filmRepository;
        this.objectsValidator = objectsValidator;
        this.categorieService = categorieService;
    }

    @Override
    @Transactional
    public FilmDTO createFilm(FilmDTO dto) {
        // Valider l'objet avant de le persister
        objectsValidator.validate(dto);

        try {
            Film film;

            // Récupérer la catégorie
            CategorieDTO categorieDTO = categorieService.getCategorieById(dto.getCategorieId());

            // Si l'ID est présent, c'est une modification
            if (dto.getId() != null && dto.getId() > 0) {
                // ⭐ RÉCUPÉRER l'entité existante pour préserver les dates
                film = filmRepository.findById(dto.getId())
                        .orElseThrow(() -> new InvalidEntityException(
                                "Film non trouvé pour modification.",
                                BusinessErrorCodes.FILM_NOT_FOUND
                        ));

                // ⭐ METTRE À JOUR seulement les champs modifiables
                film.setTitre(dto.getTitre());
                film.setDescription(dto.getDescription());
                film.setDuree(dto.getDuree());
                film.setImageUrl(dto.getImageUrl());
                film.setCategorie(categorieDTO.toEntity());

                film = filmRepository.save(film);
                log.info("Film avec l'ID {} modifié avec succès", dto.getId());
            } else {
                // ⭐ CRÉATION d'une nouvelle entité
                film = dto.toEntity();
                film.setCategorie(categorieDTO.toEntity());
                film = filmRepository.save(film);
                log.info("Nouveau film créé avec l'ID {}", film.getId());
            }

            return FilmDTO.fromEntity(film);

        } catch (InvalidEntityException e) {
            // Re-lancer l'exception si l'entité n'est pas trouvée
            throw e;
        } catch (Exception e) {
            // En cas d'échec, lancer une InvalidEntityException
            log.error("Erreur lors de la sauvegarde du film : {}", e.getMessage(), e);
            throw new InvalidEntityException(
                    "Échec de la sauvegarde du film : " + dto.getTitre(),
                    BusinessErrorCodes.FILM_CREATION_FAILED
            );
        }
    }

    @Override
    public FilmDTO getFilmById(Long id) {
        validateId(id);  // Appel à la méthode de validation de l'ID

        return filmRepository.findById(id)
                .map(FilmDTO::fromEntity)
                .orElseThrow(() -> new InvalidEntityException(
                        "Aucun film trouvé avec l'ID : " + id,
                        BusinessErrorCodes.FILM_NOT_FOUND
                ));
    }

    @Override
    public List<FilmDTO> getAllFilms() {
        List<Film> films = filmRepository.findAll();

        // ⭐ CORRECTION : Ne pas lancer d'erreur si vide
        if (films.isEmpty()) {
            log.info("Aucun film trouvé - retour liste vide");
            return new ArrayList<>();
        }

        return films.stream()
                .map(FilmDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFilm(Long id) {
        validateId(id);  // Appel à la méthode de validation de l'ID

        if (!filmRepository.existsById(id)) {
            throw new InvalidEntityException(
                    "Aucun film trouvé avec l'ID : " + id,
                    BusinessErrorCodes.FILM_NOT_FOUND
            );
        }

        filmRepository.deleteById(id);
    }

    @Override
    public FilmDTO updateFilm(Long id, FilmDTO filmDetails) {
        validateId(id);  // Appel à la méthode de validation de l'ID

        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException(
                        "Aucun film trouvé avec l'ID : " + id,
                        BusinessErrorCodes.FILM_NOT_FOUND
                ));

        // Mettre à jour les informations du film
        film.setTitre(filmDetails.getTitre());
        film.setDescription(filmDetails.getDescription());
        film.setDuree(filmDetails.getDuree());
        film.setImageUrl(filmDetails.getImageUrl());

        // Sauvegarder et retourner le FilmDTO mis à jour
        Film updatedFilm = filmRepository.save(film);
        return FilmDTO.fromEntity(updatedFilm);
    }

    // Méthode privée pour valider l'ID
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidEntityException(
                    "ID de film invalide : " + id,
                    BusinessErrorCodes.INVALID_FILM_ID
            );
        }
    }
}
