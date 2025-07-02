package com.spring.cinema.utils;

public interface Constants {

    // URL de base de l'API
    String APP_ROOT = "cinema/v1";

    // Endpoints pour Ville
    String VILLE_ENDPOINT = APP_ROOT + "/villes";
    String CREATE_VILLE_ENDPOINT = VILLE_ENDPOINT + "/create";
    String FIND_VILLE_BY_ID_ENDPOINT = VILLE_ENDPOINT + "/{idVille}";
    String FIND_ALL_VILLES_ENDPOINT = VILLE_ENDPOINT + "/all";
    String DELETE_VILLE_ENDPOINT = VILLE_ENDPOINT + "/delete/{idVille}";

    // Endpoints pour Cinéma
    String CINEMA_ENDPOINT = APP_ROOT + "/cinemas";
    String CREATE_CINEMA_ENDPOINT = CINEMA_ENDPOINT + "/create";
    String FIND_CINEMA_BY_ID_ENDPOINT = CINEMA_ENDPOINT + "/{idCinema}";
    String FIND_ALL_CINEMAS_ENDPOINT = CINEMA_ENDPOINT + "/all";
    String DELETE_CINEMA_ENDPOINT = CINEMA_ENDPOINT + "/delete/{idCinema}";

    // Endpoints pour Salle
    String SALLE_ENDPOINT = APP_ROOT + "/salles";
    String CREATE_SALLE_ENDPOINT = SALLE_ENDPOINT + "/create";
    String FIND_SALLE_BY_ID_ENDPOINT = SALLE_ENDPOINT + "/{idSalle}";
    String FIND_ALL_SALLES_ENDPOINT = SALLE_ENDPOINT + "/all";
    String DELETE_SALLE_ENDPOINT = SALLE_ENDPOINT + "/delete/{idSalle}";

    // Endpoints pour Place
    String PLACE_ENDPOINT = APP_ROOT + "/places";
    String CREATE_PLACE_ENDPOINT = PLACE_ENDPOINT + "/create";
    String FIND_PLACE_BY_ID_ENDPOINT = PLACE_ENDPOINT + "/{idPlace}";
    String FIND_ALL_PLACES_ENDPOINT = PLACE_ENDPOINT + "/all";
    String DELETE_PLACE_ENDPOINT = PLACE_ENDPOINT + "/delete/{idPlace}";
}
