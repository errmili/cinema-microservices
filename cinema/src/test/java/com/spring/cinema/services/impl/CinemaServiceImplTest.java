package com.spring.cinema.services.impl;

import com.spring.cinema.dto.CinemaDTO;
import com.spring.cinema.exception.InvalidEntityException;
import com.spring.cinema.handler.BusinessErrorCodes;
import com.spring.cinema.models.Cinema;
import com.spring.cinema.models.Ville;
import com.spring.cinema.repository.CinemaRepository;
import com.spring.cinema.repository.SaleRepository;
import com.spring.cinema.repository.VilleRepository;
import com.spring.cinema.validators.ObjectsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests Unitaires COMPLETS pour CinemaService
 * Service de gestion des cinémas avec relations (Ville, Salles)
 *
 * ✅ Tests de création
 * ✅ Tests de mise à jour
 * ✅ Tests de récupération
 * ✅ Tests de suppression
 * ✅ Tests de validation
 * ✅ Tests de gestion des relations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - CinemaService")
class CinemaServiceImplTest {

    @Mock
    private CinemaRepository cinemaRepository;

    @Mock
    private VilleRepository villeRepository;

    @Mock
    private SaleRepository salleRepository;

    @Mock
    private ObjectsValidator<CinemaDTO> objectsValidator;

    @InjectMocks
    private CinemaServiceImpl cinemaService;

    private CinemaDTO cinemaDTO;
    private Cinema cinema;
    private Ville ville;

    /**
     * Méthode utilitaire pour créer un CinemaDTO valide
     */
    private CinemaDTO createValidCinemaDTO() {
        CinemaDTO dto = new CinemaDTO();
        dto.setName("Cinéma Pathé");
        dto.setLongitude(2.3522);
        dto.setLatitude(48.8566);
        dto.setAltitude(35.0);
        dto.setVilleId(1L);
        return dto;
    }

    /**
     * Méthode utilitaire pour créer une entité Cinema valide
     */
    private Cinema createValidCinema() {
        Cinema c = new Cinema();
        c.setId(1L);
        c.setName("Cinéma Pathé");
        c.setLongitude(2.3522);
        c.setLatitude(48.8566);
        c.setAltitude(35.0);
        c.setVille(ville);
        return c;
    }

    /**
     * Méthode utilitaire pour créer une Ville
     */
    private Ville createVille() {
        Ville v = new Ville();
        v.setId(1L);
        v.setCityName("Paris");
        return v;
    }

    @BeforeEach
    void setUp() {
        ville = createVille();
        cinemaDTO = createValidCinemaDTO();
        cinema = createValidCinema();
    }

    // ==================== TESTS DE CRÉATION (save - nouveau) ====================

    @Test
    @DisplayName("Test 1 : Créer un cinema avec succès")
    void testSave_CreateNewCinema_Success() {
        // Given
        doNothing().when(objectsValidator).validate(any(CinemaDTO.class));
        when(villeRepository.findById(1L)).thenReturn(Optional.of(ville));
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(cinema);

        // When
        CinemaDTO result = cinemaService.save(cinemaDTO);

        // Then
        assertNotNull(result);
        assertEquals("Cinéma Pathé", result.getName());
        assertEquals(2.3522, result.getLongitude());
        assertEquals(48.8566, result.getLatitude());
        assertEquals(1L, result.getVilleId());

        verify(objectsValidator, times(1)).validate(cinemaDTO);
        verify(villeRepository, times(1)).findById(1L);
        verify(cinemaRepository, times(1)).save(any(Cinema.class));
    }

    @Test
    @DisplayName("Test 2 : Créer un cinema - Validation échoue (Erreur)")
    void testSave_ValidationFails_ThrowsException() {
        // Given
        doThrow(new RuntimeException("Validation error"))
                .when(objectsValidator).validate(any(CinemaDTO.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> cinemaService.save(cinemaDTO));

        verify(villeRepository, never()).findById(anyLong());
        verify(cinemaRepository, never()).save(any(Cinema.class));
    }

    @Test
    @DisplayName("Test 3 : Créer un cinema - Ville inexistante (Erreur)")
    void testSave_VilleNotFound_ThrowsException() {
        // Given
        doNothing().when(objectsValidator).validate(any(CinemaDTO.class));
        when(villeRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> cinemaService.save(cinemaDTO));

        verify(cinemaRepository, never()).save(any(Cinema.class));
    }

    @Test
    @DisplayName("Test 4 : Créer un cinema - Repository lance exception (Erreur)")
    void testSave_RepositoryThrowsException() {
        // Given
        doNothing().when(objectsValidator).validate(any(CinemaDTO.class));
        when(villeRepository.findById(1L)).thenReturn(Optional.of(ville));
        when(cinemaRepository.save(any(Cinema.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> cinemaService.save(cinemaDTO)
        );

        assertEquals("Échec de la sauvegarde du cinema.", exception.getMessage());
        assertEquals(BusinessErrorCodes.CINEMA_CREATION_FAILED, exception.getBusinessErrorCodes());
    }

    // ==================== TESTS DE MISE À JOUR (save - avec ID) ====================

    @Test
    @DisplayName("Test 5 : Mettre à jour un cinema existant avec succès")
    void testSave_UpdateExistingCinema_Success() {
        // Given
        cinemaDTO.setId(1L);
        cinemaDTO.setName("Cinéma Gaumont");
        cinemaDTO.setLongitude(2.4);

        Cinema existingCinema = createValidCinema();
        Cinema updatedCinema = createValidCinema();
        updatedCinema.setName("Cinéma Gaumont");
        updatedCinema.setLongitude(2.4);

        doNothing().when(objectsValidator).validate(any(CinemaDTO.class));
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(existingCinema));
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(updatedCinema);

        // When
        CinemaDTO result = cinemaService.save(cinemaDTO);

        // Then
        assertNotNull(result);
        assertEquals("Cinéma Gaumont", result.getName());
        assertEquals(2.4, result.getLongitude());

        verify(cinemaRepository, times(1)).findById(1L);
        verify(cinemaRepository, times(1)).save(any(Cinema.class));
    }

    @Test
    @DisplayName("Test 6 : Mettre à jour un cinema inexistant (Erreur)")
    void testSave_UpdateNonExistingCinema_ThrowsException() {
        // Given
        cinemaDTO.setId(999L);

        doNothing().when(objectsValidator).validate(any(CinemaDTO.class));
        when(cinemaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> cinemaService.save(cinemaDTO)
        );

        assertEquals("Cinema non trouvé pour modification.", exception.getMessage());
        assertEquals(BusinessErrorCodes.CINEMA_NOT_FOUND, exception.getBusinessErrorCodes());

        verify(cinemaRepository, never()).save(any(Cinema.class));
    }

    @Test
    @DisplayName("Test 7 : Mettre à jour avec changement de ville")
    void testSave_UpdateWithDifferentVille_Success() {
        // Given
        cinemaDTO.setId(1L);
        cinemaDTO.setVilleId(2L); // Changement de ville

        Ville nouvelleVille = new Ville();
        nouvelleVille.setId(2L);
        nouvelleVille.setCityName("Lyon");

        Cinema existingCinema = createValidCinema();
        Cinema updatedCinema = createValidCinema();
        updatedCinema.setVille(nouvelleVille);

        doNothing().when(objectsValidator).validate(any(CinemaDTO.class));
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(existingCinema));
        when(villeRepository.findById(2L)).thenReturn(Optional.of(nouvelleVille));
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(updatedCinema);

        // When
        CinemaDTO result = cinemaService.save(cinemaDTO);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getVilleId());

        verify(villeRepository, times(1)).findById(2L);
        verify(cinemaRepository, times(1)).save(any(Cinema.class));
    }

    @Test
    @DisplayName("Test 8 : Mettre à jour avec ville inexistante (Erreur)")
    void testSave_UpdateWithNonExistingVille_ThrowsException() {
        // Given
        cinemaDTO.setId(1L);
        cinemaDTO.setVilleId(999L);

        Cinema existingCinema = createValidCinema();

        doNothing().when(objectsValidator).validate(any(CinemaDTO.class));
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(existingCinema));
        when(villeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> cinemaService.save(cinemaDTO)
        );

        assertEquals("Ville non trouvée avec l'ID : 999", exception.getMessage());
        assertEquals(BusinessErrorCodes.VILLE_NOT_FOUND, exception.getBusinessErrorCodes());
    }

    // ==================== TESTS DE RÉCUPÉRATION PAR ID (getById) ====================

    @Test
    @DisplayName("Test 9 : Récupérer un cinema par ID avec succès")
    void testGetById_Success() {
        // Given
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));

        // When
        CinemaDTO result = cinemaService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Cinéma Pathé", result.getName());
        assertEquals(2.3522, result.getLongitude());

        verify(cinemaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test 10 : Récupérer un cinema - ID inexistant (Erreur)")
    void testGetById_NotFound() {
        // Given
        when(cinemaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> cinemaService.getById(999L)
        );

        assertEquals("Aucun cinéma trouvé avec l'ID : 999", exception.getMessage());
        assertEquals(BusinessErrorCodes.CINEMA_NOT_FOUND, exception.getBusinessErrorCodes());

        verify(cinemaRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Test 11 : Récupérer un cinema - ID null (Erreur)")
    void testGetById_NullId() {
        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> cinemaService.getById(null)
        );

        assertEquals("ID de cinéma invalide : null", exception.getMessage());
        assertEquals(BusinessErrorCodes.INVALID_CINEMA_ID, exception.getBusinessErrorCodes());

        verify(cinemaRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Test 12 : Récupérer un cinema - ID négatif (Erreur)")
    void testGetById_NegativeId() {
        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> cinemaService.getById(-1L)
        );

        assertEquals("ID de cinéma invalide : -1", exception.getMessage());
        assertEquals(BusinessErrorCodes.INVALID_CINEMA_ID, exception.getBusinessErrorCodes());

        verify(cinemaRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Test 13 : Récupérer un cinema - ID zéro (Erreur)")
    void testGetById_ZeroId() {
        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> cinemaService.getById(0L)
        );

        assertEquals("ID de cinéma invalide : 0", exception.getMessage());
        assertEquals(BusinessErrorCodes.INVALID_CINEMA_ID, exception.getBusinessErrorCodes());

        verify(cinemaRepository, never()).findById(any());
    }

    // ==================== TESTS DE RÉCUPÉRATION DE TOUS LES CINEMAS (getAll) ====================

    @Test
    @DisplayName("Test 14 : Récupérer tous les cinemas avec succès")
    void testGetAll_Success() {
        // Given
        Cinema cinema2 = new Cinema();
        cinema2.setId(2L);
        cinema2.setName("Cinéma UGC");
        cinema2.setVille(ville);

        List<Cinema> cinemas = Arrays.asList(cinema, cinema2);
        when(cinemaRepository.findAll()).thenReturn(cinemas);

        // When
        List<CinemaDTO> result = cinemaService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cinéma Pathé", result.get(0).getName());
        assertEquals("Cinéma UGC", result.get(1).getName());

        verify(cinemaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test 15 : Récupérer tous les cinemas - Liste vide (OK)")
    void testGetAll_EmptyList() {
        // Given
        when(cinemaRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<CinemaDTO> result = cinemaService.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        verify(cinemaRepository, times(1)).findAll();
    }

    // ==================== TESTS DE SUPPRESSION (delete) ====================

    @Test
    @DisplayName("Test 16 : Supprimer un cinema avec succès")
    void testDelete_Success() {
        // Given
        when(cinemaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cinemaRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> cinemaService.delete(1L));

        // Then
        verify(cinemaRepository, times(1)).existsById(1L);
        verify(cinemaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test 17 : Supprimer un cinema - ID inexistant (Erreur)")
    void testDelete_NotFound() {
        // Given
        when(cinemaRepository.existsById(999L)).thenReturn(false);

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> cinemaService.delete(999L)
        );

        assertEquals("Aucun cinéma trouvé avec l'ID : 999", exception.getMessage());
        assertEquals(BusinessErrorCodes.CINEMA_NOT_FOUND, exception.getBusinessErrorCodes());

        verify(cinemaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Test 18 : Supprimer un cinema - ID null (Erreur)")
    void testDelete_NullId() {
        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> cinemaService.delete(null)
        );

        assertEquals("ID de cinéma invalide : null", exception.getMessage());
        assertEquals(BusinessErrorCodes.INVALID_CINEMA_ID, exception.getBusinessErrorCodes());

        verify(cinemaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Test 19 : Supprimer un cinema - ID négatif (Erreur)")
    void testDelete_NegativeId() {
        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> cinemaService.delete(-1L)
        );

        assertEquals("ID de cinéma invalide : -1", exception.getMessage());
        assertEquals(BusinessErrorCodes.INVALID_CINEMA_ID, exception.getBusinessErrorCodes());

        verify(cinemaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Test 20 : Supprimer un cinema - ID zéro (Erreur)")
    void testDelete_ZeroId() {
        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> cinemaService.delete(0L)
        );

        assertEquals("ID de cinéma invalide : 0", exception.getMessage());
        assertEquals(BusinessErrorCodes.INVALID_CINEMA_ID, exception.getBusinessErrorCodes());

        verify(cinemaRepository, never()).deleteById(any());
    }

    // ==================== TESTS DE MÉTHODE getNombreSalles ====================

    @Test
    @DisplayName("Test 21 : Obtenir le nombre de salles d'un cinema")
    void testGetNombreSalles_Success() {
        // Given
        when(salleRepository.countByCinemaId(1L)).thenReturn(5);

        // When
        int result = cinemaService.getNombreSalles(1L);

        // Then
        assertEquals(5, result);
        verify(salleRepository, times(1)).countByCinemaId(1L);
    }

    @Test
    @DisplayName("Test 22 : Obtenir le nombre de salles - Aucune salle")
    void testGetNombreSalles_NoSalles() {
        // Given
        when(salleRepository.countByCinemaId(1L)).thenReturn(0);

        // When
        int result = cinemaService.getNombreSalles(1L);

        // Then
        assertEquals(0, result);
        verify(salleRepository, times(1)).countByCinemaId(1L);
    }

    // ==================== TESTS DE VALIDATION DES DONNÉES ====================

    @Test
    @DisplayName("Test 23 : Créer un cinema - Nom null (Validation)")
    void testSave_NullName_ValidationFails() {
        // Given
        cinemaDTO.setName(null);

        doThrow(new RuntimeException("Le nom du cinéma ne doit pas être vide"))
                .when(objectsValidator).validate(any(CinemaDTO.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> cinemaService.save(cinemaDTO));
        verify(cinemaRepository, never()).save(any(Cinema.class));
    }

    @Test
    @DisplayName("Test 24 : Créer un cinema - Ville ID null (Validation)")
    void testSave_NullVilleId_ValidationFails() {
        // Given
        cinemaDTO.setVilleId(null);

        doThrow(new RuntimeException("L'id de la ville ne doit pas être vide"))
                .when(objectsValidator).validate(any(CinemaDTO.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> cinemaService.save(cinemaDTO));
        verify(cinemaRepository, never()).save(any(Cinema.class));
    }

    // ==================== TESTS DE CAS LIMITES ====================

    @Test
    @DisplayName("Test 25 : Créer un cinema avec altitude zéro")
    void testSave_ZeroAltitude_Success() {
        // Given
        cinemaDTO.setAltitude(0.0);
        cinema.setAltitude(0.0);

        doNothing().when(objectsValidator).validate(any(CinemaDTO.class));
        when(villeRepository.findById(1L)).thenReturn(Optional.of(ville));
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(cinema);

        // When
        CinemaDTO result = cinemaService.save(cinemaDTO);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getAltitude());
        verify(cinemaRepository, times(1)).save(any(Cinema.class));
    }
}