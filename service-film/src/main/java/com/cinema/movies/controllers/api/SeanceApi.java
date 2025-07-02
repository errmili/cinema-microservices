package com.cinema.movies.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.cinema.movies.dto.SeanceDTO;
import com.cinema.movies.utils.Constants;

import java.util.List;

public interface SeanceApi {

    @Operation(
            summary = "Enregistrer une séance",
            description = "Cette méthode permet d'enregistrer ou modifier une séance"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Séance créée / modifiée avec succès"),
            @ApiResponse(responseCode = "400", description = "Séance invalide")
    })
    @PostMapping(value = Constants.CREATE_SEANCE_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    SeanceDTO save(@RequestBody SeanceDTO seanceDTO);

    @Operation(
            summary = "Rechercher une séance par ID",
            description = "Cette méthode permet de chercher une séance par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Séance trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune séance trouvée avec l'ID fourni")
    })
    @GetMapping(value = Constants.FIND_SEANCE_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    SeanceDTO findById(@PathVariable("idSeance") Long idSeance);

    @Operation(
            summary = "Renvoi la liste des séances",
            description = "Cette méthode permet de chercher et renvoyer la liste des séances qui existent dans la BDD"
    )
    @ApiResponse(responseCode = "200", description = "Liste des séances récupérée avec succès")
    @GetMapping(value = Constants.FIND_ALL_SEANCES_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<SeanceDTO> findAll();

    @Operation(
            summary = "Supprimer une séance",
            description = "Cette méthode permet de supprimer une séance par ID"
    )
    @ApiResponse(responseCode = "200", description = "Séance supprimée avec succès")
    @DeleteMapping(value = Constants.DELETE_SEANCE_ENDPOINT)
    void delete(@PathVariable("idSeance") Long idSeance);
}