package com.cinema.movies.controllers.api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

import com.cinema.movies.dto.CategorieDTO;
import com.cinema.movies.utils.Constants;

//import static com.spring.teststock.utils.Constants.APP_ROOT;
public interface CategoryApi {

    @Operation(
            summary = "Enregistrer une catégorie",
            description = "Cette méthode permet d'enregistrer ou modifier une catégorie"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet catégorie créé / modifié"),
            @ApiResponse(responseCode = "400", description = "L'objet catégorie n'est pas valide")
    })
    @PostMapping(value = Constants.CREATE_CATEGORIE_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CategorieDTO save(@RequestBody CategorieDTO categorieDTO);

    @Operation(
            summary = "Rechercher une catégorie par ID",
            description = "Cette méthode permet de chercher une catégorie par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La catégorie a été trouvée dans la BDD"),
            @ApiResponse(responseCode = "404", description = "Aucune catégorie n'existe dans la BDD avec l'ID fourni")
    })
    @GetMapping(value = Constants.FIND_CATEGORIE_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    CategorieDTO findById(@PathVariable("idCategorie") Long idCategorie);

//    @GetMapping(value = Constants.FIND_CATEGORIE_BY_CODE_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(value = "Rechercher une catégorie par CODE", notes = "Cette méthode permet de chercher une catégorie par son CODE", response = CategorieDTO.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "La catégorie a été trouvée dans la BDD"),
//            @ApiResponse(code = 404, message = "Aucune catégorie n'existe dans la BDD avec le CODE fourni")
//    })
//    CategorieDTO findByCode(@PathVariable("codeCategorie") String codeCategorie);

    @Operation(
            summary = "Renvoi la liste des catégories",
            description = "Cette méthode permet de chercher et renvoyer la liste des catégories qui existent dans la BDD"
    )
    @ApiResponse(responseCode = "200", description = "La liste des catégories / Une liste vide")
    @GetMapping(value = Constants.FIND_ALL_CATEGORIES_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<CategorieDTO> findAll();

    @Operation(
            summary = "Supprimer une catégorie",
            description = "Cette méthode permet de supprimer une catégorie par ID"
    )
    @ApiResponse(responseCode = "200", description = "La catégorie a été supprimée")
    @DeleteMapping(value = Constants.DELETE_CATEGORIE_ENDPOINT)
    void delete(@PathVariable("idCategorie") Long id);
}

