package com.cinema.movies.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.cinema.movies.dto.FilmDTO;
import com.cinema.movies.utils.Constants;

public interface FilmApi {

    @Operation(
            summary = "Enregistrer un film",
            description = "Cette méthode permet d'enregistrer un film"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film enregistré avec succès"),
            @ApiResponse(responseCode = "400", description = "Film invalide")
    })
    @PostMapping(value = Constants.CREATE_FILM_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    FilmDTO save(@RequestBody FilmDTO filmDTO);

    @Operation(
            summary = "Rechercher un film par ID",
            description = "Cette méthode permet de chercher un film par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film trouvé"),
            @ApiResponse(responseCode = "404", description = "Film non trouvé")
    })
    @GetMapping(value = Constants.FIND_FILM_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    FilmDTO findById(@PathVariable("idFilm") Long idFilm);

    @Operation(
            summary = "Renvoi la liste des films",
            description = "Cette méthode permet de récupérer tous les films disponibles"
    )
    @ApiResponse(responseCode = "200", description = "Liste des films récupérée avec succès")
    @GetMapping(value = Constants.FIND_ALL_FILMS_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<FilmDTO> findAll();

    @Operation(
            summary = "Mettre à jour un film",
            description = "Cette méthode permet de modifier un film existant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film mis à jour"),
            @ApiResponse(responseCode = "404", description = "Film non trouvé")
    })
    @PutMapping(value = Constants.UPDATE_FILM_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    FilmDTO update(@PathVariable("idFilm") Long idFilm, @RequestBody FilmDTO filmDTO);

    @Operation(
            summary = "Supprimer un film",
            description = "Cette méthode permet de supprimer un film par ID"
    )
    @ApiResponse(responseCode = "200", description = "Film supprimé avec succès")
    @DeleteMapping(value = Constants.DELETE_FILM_ENDPOINT)
    void delete(@PathVariable("idFilm") Long idFilm);
}