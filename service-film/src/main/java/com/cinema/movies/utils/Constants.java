package com.cinema.movies.utils;

public interface Constants {

    // URL de base de l'API
    String APP_ROOT = "movies/v1"; // Ajuster si nécessaire, cela pourrait être "movies/v1" ou autre.

    // Endpoints pour Film
    String FILM_ENDPOINT = APP_ROOT + "/films";
    String CREATE_FILM_ENDPOINT = FILM_ENDPOINT + "/create";
    String FIND_FILM_BY_ID_ENDPOINT = FILM_ENDPOINT + "/{idFilm}";
    String FIND_ALL_FILMS_ENDPOINT = FILM_ENDPOINT + "/all";
    String DELETE_FILM_ENDPOINT = FILM_ENDPOINT + "/delete/{idFilm}";
    String UPDATE_FILM_ENDPOINT = FILM_ENDPOINT + "/update/{idFilm}";

    // Endpoints pour Catégorie
    String CATEGORIE_ENDPOINT = APP_ROOT + "/categories";
    String CREATE_CATEGORIE_ENDPOINT = CATEGORIE_ENDPOINT + "/create";
    String FIND_CATEGORIE_BY_ID_ENDPOINT = CATEGORIE_ENDPOINT + "/{idCategorie}";
    String FIND_CATEGORIE_BY_CODE_ENDPOINT = CATEGORIE_ENDPOINT + "/filter/{codeCategorie}";
    String FIND_ALL_CATEGORIES_ENDPOINT = CATEGORIE_ENDPOINT + "/all";
    String DELETE_CATEGORIE_ENDPOINT = CATEGORIE_ENDPOINT + "/delete/{idCategorie}";

    // Endpoints pour Projection
    String PROJECTION_ENDPOINT = APP_ROOT + "/projections";
    String CREATE_PROJECTION_ENDPOINT = PROJECTION_ENDPOINT + "/create";
    String FIND_PROJECTION_BY_ID_ENDPOINT = PROJECTION_ENDPOINT + "/{idProjection}";
    String FIND_ALL_PROJECTIONS_ENDPOINT = PROJECTION_ENDPOINT + "/all";
    String DELETE_PROJECTION_ENDPOINT = PROJECTION_ENDPOINT + "/delete/{idProjection}";

    // Endpoints pour Séance
    String SEANCE_ENDPOINT = APP_ROOT + "/seances";
    String CREATE_SEANCE_ENDPOINT = SEANCE_ENDPOINT + "/create";
    String FIND_SEANCE_BY_ID_ENDPOINT = SEANCE_ENDPOINT + "/{idSeance}";
    String FIND_ALL_SEANCES_ENDPOINT = SEANCE_ENDPOINT + "/all";
    String DELETE_SEANCE_ENDPOINT = SEANCE_ENDPOINT + "/delete/{idSeance}";

}
