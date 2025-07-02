package com.cinema.movies.services;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cinema.movies.dto.FilmDTO;
import com.cinema.movies.models.Categorie;
import com.cinema.movies.models.Film;
import com.cinema.movies.repository.FilmRepository;
import com.cinema.movies.services.impl.FilmServiceImpl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FilmServiceTest {


//    @Mock
//    private FilmRepository filmRepository; // Mock du repository
//
//    private FilmService filmService; // FilmService dans lequel nous testons la logique
//
//    @BeforeEach
//    void setUp() {
//        filmService = new FilmServiceImpl(filmRepository); // avec constructeur explicite
//    }
//
//    @Test
//    public void testCreateFilm() {
//        // Arrange
//        Categorie cat = Categorie.builder().nom("SF").build();
//
////        Film film = Film.builder()
////                .titre("Inception")
////                .description("A sci-fi thriller")
////                .duree(148)
////                .imageUrl("inception.jpg")
////                .categorie(cat)
////                .build();
//
//        // Créer un FilmDTO pour tester la création
//        FilmDTO filmDTO = new FilmDTO();
//        filmDTO.setTitre("Inception");
//        filmDTO.setDescription("A sci-fi thriller");
//        filmDTO.setDuree(148);
//        filmDTO.setImageUrl("inception.jpg");
//        filmDTO.setCategorieId(1L);  // Utiliser l'ID de la catégorie ici
//
//        Film film = filmDTO.toEntity();  // Convertir le FilmDTO en entité Film
//
//        Film savedFilm = film.toBuilder().id(1L).build();
//        // Act : Appeler la méthode pour créer un film
//        when(filmRepository.save(film)).thenReturn(savedFilm);  // Simulation de l'appel au repository
//        // Act
//        FilmDTO result = filmService.createFilm(filmDTO);
//
//        // Assert : Vérifier que l'id du film est généré et que le titre est correct
//        assertNotNull(result.getId());
//        assertEquals("Inception", result.getTitre()); // Vérification du titre
//        verify(filmRepository).save(film);  // Vérifier que save a bien été appelé avec le film
//
//
//    }
}
