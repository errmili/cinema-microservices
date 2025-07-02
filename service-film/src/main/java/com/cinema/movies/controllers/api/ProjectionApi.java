package com.cinema.movies.controllers.api;

import com.cinema.movies.dto.ProjectionDTO;
import com.cinema.movies.utils.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ProjectionApi {

    @Operation(
            summary = "Enregistrer une projection",
            description = "Cette méthode permet d'enregistrer une projection"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projection enregistrée avec succès"),
            @ApiResponse(responseCode = "400", description = "Projection invalide")
    })
    @PostMapping(value = Constants.CREATE_PROJECTION_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ProjectionDTO save(@RequestBody ProjectionDTO projectionDTO);

    @Operation(
            summary = "Rechercher une projection par ID",
            description = "Cette méthode permet de chercher une projection par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projection trouvée"),
            @ApiResponse(responseCode = "404", description = "Projection non trouvée")
    })
    @GetMapping(value = Constants.FIND_PROJECTION_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    ProjectionDTO findById(@PathVariable("idProjection") Long idProjection);

    @Operation(
            summary = "Renvoi la liste des projections",
            description = "Cette méthode permet de récupérer la liste de toutes les projections"
    )
    @ApiResponse(responseCode = "200", description = "Liste des projections récupérée avec succès")
    @GetMapping(value = Constants.FIND_ALL_PROJECTIONS_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<ProjectionDTO> findAll();

    @Operation(
            summary = "Supprimer une projection",
            description = "Cette méthode permet de supprimer une projection par ID"
    )
    @ApiResponse(responseCode = "200", description = "Projection supprimée avec succès")
    @DeleteMapping(value = Constants.DELETE_PROJECTION_ENDPOINT)
    void delete(@PathVariable("idProjection") Long idProjection);
}