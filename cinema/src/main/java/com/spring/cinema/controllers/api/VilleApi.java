package com.spring.cinema.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.spring.cinema.dto.VilleDTO;
import com.spring.cinema.utils.Constants;

public interface VilleApi {

    @Operation(summary = "Créer une ville", description = "Créer ou modifier une ville")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ville créée / modifiée"),
            @ApiResponse(responseCode = "400", description = "Ville non valide")
    })
    @PostMapping(value = Constants.CREATE_VILLE_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    VilleDTO save(@RequestBody VilleDTO dto);

    @Operation(summary = "Rechercher une ville par ID", description = "Trouver une ville avec son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ville trouvée"),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée")
    })
    @GetMapping(value = Constants.FIND_VILLE_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    VilleDTO getById(@PathVariable("idVille") Long id);

    @Operation(summary = "Lister les villes", description = "Retourne toutes les villes")
    @ApiResponse(responseCode = "200", description = "Liste des villes ou vide")
    @GetMapping(value = Constants.FIND_ALL_VILLES_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<VilleDTO> getAll();

    @Operation(summary = "Supprimer une ville", description = "Supprimer une ville avec son ID")
    @ApiResponse(responseCode = "200", description = "Ville supprimée")
    @DeleteMapping(value = Constants.DELETE_VILLE_ENDPOINT)
    void delete(@PathVariable("idVille") Long id);
}