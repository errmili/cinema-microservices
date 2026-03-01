package com.cinema.movies.services;

import com.cinema.movies.dto.CategorieDTO;
import com.cinema.movies.dto.FilmDTO;
import com.cinema.movies.exception.InvalidEntityException;
import com.cinema.movies.handler.BusinessErrorCodes;
import com.cinema.movies.models.Categorie;
import com.cinema.movies.models.Film;
import com.cinema.movies.repository.FilmRepository;
import com.cinema.movies.services.impl.FilmServiceImpl;
import com.cinema.movies.validators.ObjectsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests Unitaires pour FilmService
 * Ces tests vérifient la logique métier du service Film en mockant toutes les dépendances
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - FilmService")
class FilmServiceTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private ObjectsValidator<FilmDTO> objectsValidator;

    @Mock
    private CategorieService categorieService;

    @InjectMocks
    private FilmServiceImpl filmService;

    private FilmDTO filmDTO;
    private Film film;
    private Categorie categorie;
    private CategorieDTO categorieDTO;

    @BeforeEach
    void setUp() {
        // Préparer une catégorie
        categorie = Categorie.builder()
                .nom("Action")
                .build();
        categorie.setId(1L);
        categorie.setCreationDate(Instant.now());

        categorieDTO = new CategorieDTO();
        categorieDTO.setId(1L);
        categorieDTO.setNom("Action");

        // Préparer un DTO de film
        filmDTO = new FilmDTO();
        filmDTO.setTitre("Inception");
        filmDTO.setDescription("A mind-bending thriller");
        filmDTO.setDuree(148);
        filmDTO.setImageUrl("inception.jpg");
        filmDTO.setCategorieId(1L);

        // Préparer une entité Film
        film = Film.builder()
                .titre("Inception")
                .description("A mind-bending thriller")
                .duree(148)
                .imageUrl("inception.jpg")
                .categorie(categorie)
                .build();
        film.setId(1L);
        film.setCreationDate(Instant.now());
    }

    // ==================== TESTS DE CRÉATION ====================

    @Test
    @DisplayName("Test 1 : Créer un film avec succès")
    void testCreateFilm_Success() {
        // Given
        doNothing().when(objectsValidator).validate(any(FilmDTO.class));
        when(categorieService.getCategorieById(anyLong())).thenReturn(categorieDTO);
        when(filmRepository.save(any(Film.class))).thenReturn(film);

        // When
        FilmDTO result = filmService.createFilm(filmDTO);

        // Then
        assertNotNull(result);
        assertEquals("Inception", result.getTitre());
        assertEquals(148, result.getDuree());
        assertEquals(1L, result.getCategorieId());

        verify(objectsValidator, times(1)).validate(filmDTO);
        verify(categorieService, times(1)).getCategorieById(1L);
        verify(filmRepository, times(1)).save(any(Film.class));
    }

    @Test
    @DisplayName("Test 2 : Créer un film avec catégorie inexistante - Doit échouer")
    void testCreateFilm_CategoryNotFound() {
        // Given
        doNothing().when(objectsValidator).validate(any(FilmDTO.class));
        when(categorieService.getCategorieById(anyLong()))
                .thenThrow(new InvalidEntityException("Catégorie non trouvée", BusinessErrorCodes.CATEGORY_NOT_FOUND));

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> filmService.createFilm(filmDTO)
        );

        assertEquals("Catégorie non trouvée", exception.getMessage());
        verify(filmRepository, never()).save(any(Film.class));
    }

    @Test
    @DisplayName("Test 3 : Créer un film avec validation échouée")
    void testCreateFilm_ValidationFails() {
        // Given
        doThrow(new RuntimeException("Validation error"))
                .when(objectsValidator).validate(any(FilmDTO.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> filmService.createFilm(filmDTO));
        verify(filmRepository, never()).save(any(Film.class));
    }

    // ==================== TESTS DE RÉCUPÉRATION PAR ID ====================

    @Test
    @DisplayName("Test 4 : Récupérer un film par ID avec succès")
    void testGetFilmById_Success() {
        // Given
        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));

        // When
        FilmDTO result = filmService.getFilmById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Inception", result.getTitre());
        assertEquals(148, result.getDuree());

        verify(filmRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test 5 : Récupérer un film avec ID inexistant - Doit échouer")
    void testGetFilmById_NotFound() {
        // Given
        when(filmRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> filmService.getFilmById(999L)
        );

        assertTrue(exception.getMessage().contains("Aucun film trouvé avec l'ID : 999"));
        verify(filmRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Test 6 : Récupérer un film avec ID null - Doit échouer")
    void testGetFilmById_NullId() {
        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> filmService.getFilmById(null)
        );

        assertTrue(exception.getMessage().contains("ID de film invalide"));
        verify(filmRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Test 7 : Récupérer un film avec ID négatif - Doit échouer")
    void testGetFilmById_NegativeId() {
        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> filmService.getFilmById(-1L)
        );

        assertTrue(exception.getMessage().contains("ID de film invalide"));
        verify(filmRepository, never()).findById(any());
    }

    // ==================== TESTS DE RÉCUPÉRATION DE TOUS LES FILMS ====================

    @Test
    @DisplayName("Test 8 : Récupérer tous les films avec succès")
    void testGetAllFilms_Success() {
        // Given
        Film film2 = Film.builder()
                .titre("The Matrix")
                .description("Reality simulation")
                .duree(136)
                .imageUrl("matrix.jpg")
                .categorie(categorie)
                .build();
        film2.setId(2L);

        List<Film> films = Arrays.asList(film, film2);
        when(filmRepository.findAll()).thenReturn(films);

        // When
        List<FilmDTO> result = filmService.getAllFilms();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Inception", result.get(0).getTitre());
        assertEquals("The Matrix", result.get(1).getTitre());

        verify(filmRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test 9 : Récupérer tous les films - Liste vide")
    void testGetAllFilms_EmptyList() {
        // Given
        when(filmRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<FilmDTO> result = filmService.getAllFilms();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(filmRepository, times(1)).findAll();
    }

    // ==================== TESTS DE MISE À JOUR ====================

    @Test
    @DisplayName("Test 10 : Mettre à jour un film avec succès")
    void testUpdateFilm_Success() {
        // Given
        FilmDTO updateDTO = new FilmDTO();
        updateDTO.setTitre("Inception Updated");
        updateDTO.setDescription("New description");
        updateDTO.setDuree(150);
        updateDTO.setImageUrl("new_inception.jpg");
        updateDTO.setCategorieId(1L);

        Film updatedFilm = Film.builder()
                .titre("Inception Updated")
                .description("New description")
                .duree(150)
                .imageUrl("new_inception.jpg")
                .categorie(categorie)
                .build();
        updatedFilm.setId(1L);

        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
        when(filmRepository.save(any(Film.class))).thenReturn(updatedFilm);

        // When
        FilmDTO result = filmService.updateFilm(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("Inception Updated", result.getTitre());
        assertEquals(150, result.getDuree());

        verify(filmRepository, times(1)).findById(1L);
        verify(filmRepository, times(1)).save(any(Film.class));
    }

    @Test
    @DisplayName("Test 11 : Mettre à jour un film inexistant - Doit échouer")
    void testUpdateFilm_NotFound() {
        // Given
        FilmDTO updateDTO = new FilmDTO();
        updateDTO.setTitre("Updated Title");

        when(filmRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> filmService.updateFilm(999L, updateDTO)
        );

        assertTrue(exception.getMessage().contains("Aucun film trouvé avec l'ID : 999"));
        verify(filmRepository, never()).save(any(Film.class));
    }

    @Test
    @DisplayName("Test 12 : Mettre à jour un film avec ID null - Doit échouer")
    void testUpdateFilm_NullId() {
        // Given
        FilmDTO updateDTO = new FilmDTO();

        // When & Then
        assertThrows(InvalidEntityException.class, () -> filmService.updateFilm(null, updateDTO));
        verify(filmRepository, never()).save(any(Film.class));
    }

    // ==================== TESTS DE SUPPRESSION ====================

    @Test
    @DisplayName("Test 13 : Supprimer un film avec succès")
    void testDeleteFilm_Success() {
        // Given
        when(filmRepository.existsById(1L)).thenReturn(true);
        doNothing().when(filmRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> filmService.deleteFilm(1L));

        // Then
        verify(filmRepository, times(1)).existsById(1L);
        verify(filmRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test 14 : Supprimer un film inexistant - Doit échouer")
    void testDeleteFilm_NotFound() {
        // Given
        when(filmRepository.existsById(999L)).thenReturn(false);

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> filmService.deleteFilm(999L)
        );

        assertTrue(exception.getMessage().contains("Aucun film trouvé avec l'ID : 999"));
        verify(filmRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Test 15 : Supprimer un film avec ID null - Doit échouer")
    void testDeleteFilm_NullId() {
        // When & Then
        assertThrows(InvalidEntityException.class, () -> filmService.deleteFilm(null));
        verify(filmRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Test 16 : Supprimer un film avec ID négatif - Doit échouer")
    void testDeleteFilm_NegativeId() {
        // When & Then
        assertThrows(InvalidEntityException.class, () -> filmService.deleteFilm(-5L));
        verify(filmRepository, never()).deleteById(any());
    }

    // ==================== TEST DE MODIFICATION (createFilm avec ID) ====================

    @Test
    @DisplayName("Test 17 : Modifier un film existant via createFilm")
    void testCreateFilm_UpdateExisting() {
        // Given
        filmDTO.setId(1L); // ID présent = modification
        filmDTO.setTitre("Inception - Director's Cut");

        doNothing().when(objectsValidator).validate(any(FilmDTO.class));
        when(categorieService.getCategorieById(anyLong())).thenReturn(categorieDTO);
        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
        when(filmRepository.save(any(Film.class))).thenReturn(film);

        // When
        FilmDTO result = filmService.createFilm(filmDTO);

        // Then
        assertNotNull(result);
        verify(filmRepository, times(1)).findById(1L);
        verify(filmRepository, times(1)).save(any(Film.class));
    }

    @Test
    @DisplayName("Test 18 : Modifier un film inexistant via createFilm - Doit échouer")
    void testCreateFilm_UpdateNonExisting() {
        // Given
        filmDTO.setId(999L);

        doNothing().when(objectsValidator).validate(any(FilmDTO.class));
        when(categorieService.getCategorieById(anyLong())).thenReturn(categorieDTO);
        when(filmRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> filmService.createFilm(filmDTO)
        );

        assertTrue(exception.getMessage().contains("Film non trouvé pour modification"));
        verify(filmRepository, never()).save(any(Film.class));
    }
}




//package com.cinema.movies.services;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.cinema.movies.dto.FilmDTO;
//import com.cinema.movies.models.Categorie;
//import com.cinema.movies.models.Film;
//import com.cinema.movies.repository.FilmRepository;
//import com.cinema.movies.services.impl.FilmServiceImpl;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//public class FilmServiceTest {
//
//
////    @Mock
////    private FilmRepository filmRepository; // Mock du repository
////
////    private FilmService filmService; // FilmService dans lequel nous testons la logique
////
////    @BeforeEach
////    void setUp() {
////        filmService = new FilmServiceImpl(filmRepository); // avec constructeur explicite
////    }
////
////    @Test
////    public void testCreateFilm() {
////        // Arrange
////        Categorie cat = Categorie.builder().nom("SF").build();
////
//////        Film film = Film.builder()
//////                .titre("Inception")
//////                .description("A sci-fi thriller")
//////                .duree(148)
//////                .imageUrl("inception.jpg")
//////                .categorie(cat)
//////                .build();
////
////        // Créer un FilmDTO pour tester la création
////        FilmDTO filmDTO = new FilmDTO();
////        filmDTO.setTitre("Inception");
////        filmDTO.setDescription("A sci-fi thriller");
////        filmDTO.setDuree(148);
////        filmDTO.setImageUrl("inception.jpg");
////        filmDTO.setCategorieId(1L);  // Utiliser l'ID de la catégorie ici
////
////        Film film = filmDTO.toEntity();  // Convertir le FilmDTO en entité Film
////
////        Film savedFilm = film.toBuilder().id(1L).build();
////        // Act : Appeler la méthode pour créer un film
////        when(filmRepository.save(film)).thenReturn(savedFilm);  // Simulation de l'appel au repository
////        // Act
////        FilmDTO result = filmService.createFilm(filmDTO);
////
////        // Assert : Vérifier que l'id du film est généré et que le titre est correct
////        assertNotNull(result.getId());
////        assertEquals("Inception", result.getTitre()); // Vérification du titre
////        verify(filmRepository).save(film);  // Vérifier que save a bien été appelé avec le film
////
////
////    }
//}
