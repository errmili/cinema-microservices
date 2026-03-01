package com.cinema.booking.application.usecase;

import com.cinema.booking.application.dto.TicketDTO;
import com.cinema.booking.application.exception.InvalidEntityException;
import com.cinema.booking.handler.BusinessErrorCodes;
import com.cinema.booking.domain.models.Ticket;
import com.cinema.booking.domain.repository.TicketRepository;
import com.cinema.booking.infrastructure.validators.ObjectsValidator;
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
 * Tests Unitaires COMPLETS pour TicketService
 * Ces tests vérifient la logique métier du service Ticket en mockant toutes les dépendances
 *
 * ✅ Tests corrigés par rapport à la version initiale
 * ✅ Couverture complète : Création, Récupération, Liste, Suppression, Vérification
 * ✅ Tests d'erreurs : Validations, IDs invalides, Exceptions
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - TicketService")
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ObjectsValidator<TicketDTO> objectsValidator;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private TicketDTO ticketDTO;
    private Ticket ticket;

    /**
     * Méthode utilitaire pour créer un TicketDTO valide
     * Évite la duplication de code dans les tests
     */
    private TicketDTO createValidTicketDTO() {
        return TicketDTO.builder()
                .nomClient("John Doe")
                .codePaiement("PAY123456")
                .prix(15.50)
                .projectionId(100L)
                .placeId(25L)
                .reserve(false)
                .build();
    }

    /**
     * Méthode utilitaire pour créer une entité Ticket valide
     */
    private Ticket createValidTicket() {
        Ticket t = new Ticket();
        t.setId(1L);
        t.setNomClient("John Doe");
        t.setCodePaiement("PAY123456");
        t.setPrix(15.50);
        t.setProjectionId(100L);
        t.setPlaceId(25L);
        t.setReserve(false);
        t.setCreationDate(Instant.now());
        return t;
    }

    @BeforeEach
    void setUp() {
        ticketDTO = createValidTicketDTO();
        ticket = createValidTicket();
    }

    // ==================== TESTS DE CRÉATION (save) ====================

    @Test
    @DisplayName("Test 1 : Créer un ticket avec succès")
    void testSave_CreateNewTicket_Success() {
        // Given
        doNothing().when(objectsValidator).validate(any(TicketDTO.class));

        // ✅ CORRECTION : Utiliser la bonne méthode avec projectionId ET placeId
        when(ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(
                ticketDTO.getProjectionId(),
                ticketDTO.getPlaceId()
        )).thenReturn(false);

        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // When
        TicketDTO result = ticketService.save(ticketDTO);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getNomClient());
        assertEquals("PAY123456", result.getCodePaiement());
        assertEquals(15.50, result.getPrix());
        assertEquals(100L, result.getProjectionId());
        assertEquals(25L, result.getPlaceId());

        verify(objectsValidator, times(1)).validate(ticketDTO);
        verify(ticketRepository, times(1)).existsByProjectionIdAndPlaceIdAndReserveTrue(100L, 25L);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Test 2 : Créer un ticket - Place déjà réservée (Erreur)")
    void testSave_PlaceAlreadyReserved_ThrowsException() {
        // Given
        doNothing().when(objectsValidator).validate(any(TicketDTO.class));

        // Simuler que la place est déjà réservée
        when(ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(
                ticketDTO.getProjectionId(),
                ticketDTO.getPlaceId()
        )).thenReturn(true);

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> ticketService.save(ticketDTO)
        );

        assertEquals("Cette place est déjà réservée.", exception.getMessage());
        assertEquals(BusinessErrorCodes.TICKET_CREATION_FAILED, exception.getBusinessErrorCodes());

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Test 3 : Créer un ticket - Validation échoue (Erreur)")
    void testSave_ValidationFails_ThrowsException() {
        // Given
        doThrow(new RuntimeException("Validation error"))
                .when(objectsValidator).validate(any(TicketDTO.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> ticketService.save(ticketDTO));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Test 4 : Créer un ticket - Repository lance exception (Erreur)")
    void testSave_RepositoryThrowsException() {
        // Given
        doNothing().when(objectsValidator).validate(any(TicketDTO.class));
        when(ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(anyLong(), anyLong()))
                .thenReturn(false);
        when(ticketRepository.save(any(Ticket.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> ticketService.save(ticketDTO)
        );

        assertEquals("Échec de la sauvegarde du ticket.", exception.getMessage());
        assertEquals(BusinessErrorCodes.TICKET_CREATION_FAILED, exception.getBusinessErrorCodes());
    }

    // ==================== TESTS DE MISE À JOUR (save avec ID) ====================

    @Test
    @DisplayName("Test 5 : Mettre à jour un ticket existant avec succès")
    void testSave_UpdateExistingTicket_Success() {
        // Given
        ticketDTO.setId(1L); // ID présent = mise à jour
        ticketDTO.setNomClient("Jane Smith");
        ticketDTO.setPrix(20.0);

        Ticket existingTicket = createValidTicket();
        Ticket updatedTicket = createValidTicket();
        updatedTicket.setNomClient("Jane Smith");
        updatedTicket.setPrix(20.0);

        doNothing().when(objectsValidator).validate(any(TicketDTO.class));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(existingTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(updatedTicket);

        // When
        TicketDTO result = ticketService.save(ticketDTO);

        // Then
        assertNotNull(result);
        assertEquals("Jane Smith", result.getNomClient());
        assertEquals(20.0, result.getPrix());

        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Test 6 : Mettre à jour un ticket inexistant (Erreur)")
    void testSave_UpdateNonExistingTicket_ThrowsException() {
        // Given
        ticketDTO.setId(999L);

        doNothing().when(objectsValidator).validate(any(TicketDTO.class));
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> ticketService.save(ticketDTO)
        );

        assertEquals("Ticket non trouvé pour modification.", exception.getMessage());
        assertEquals(BusinessErrorCodes.TICKET_NOT_FOUND, exception.getBusinessErrorCodes());

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    // ==================== TESTS DE RÉCUPÉRATION PAR ID (getById) ====================

    @Test
    @DisplayName("Test 7 : Récupérer un ticket par ID avec succès")
    void testGetById_Success() {
        // Given
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // When
        TicketDTO result = ticketService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getNomClient());
        assertEquals(15.50, result.getPrix());

        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test 8 : Récupérer un ticket - ID inexistant (Erreur)")
    void testGetById_NotFound() {
        // Given
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> ticketService.getById(999L)
        );

        assertEquals("Aucun ticket trouvé avec l'ID : 999", exception.getMessage());
        assertEquals(BusinessErrorCodes.TICKET_NOT_FOUND, exception.getBusinessErrorCodes());

        verify(ticketRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Test 9 : Récupérer un ticket - ID null (Erreur)")
    void testGetById_NullId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.getById(null)
        );

        assertEquals("Invalid ID: null", exception.getMessage());
        verify(ticketRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Test 10 : Récupérer un ticket - ID négatif (Erreur)")
    void testGetById_NegativeId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.getById(-1L)
        );

        assertEquals("Invalid ID: -1", exception.getMessage());
        verify(ticketRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Test 11 : Récupérer un ticket - ID zéro (Erreur)")
    void testGetById_ZeroId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.getById(0L)
        );

        assertEquals("Invalid ID: 0", exception.getMessage());
        verify(ticketRepository, never()).findById(any());
    }

    // ==================== TESTS DE RÉCUPÉRATION DE TOUS LES TICKETS (getAll) ====================

    @Test
    @DisplayName("Test 12 : Récupérer tous les tickets avec succès")
    void testGetAll_Success() {
        // Given
        Ticket ticket2 = createValidTicket();
        ticket2.setId(2L);
        ticket2.setNomClient("Jane Smith");

        List<Ticket> tickets = Arrays.asList(ticket, ticket2);
        when(ticketRepository.findAll()).thenReturn(tickets);

        // When
        List<TicketDTO> result = ticketService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getNomClient());
        assertEquals("Jane Smith", result.get(1).getNomClient());

        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test 13 : Récupérer tous les tickets - Liste vide (OK)")
    void testGetAll_EmptyList() {
        // Given
        when(ticketRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<TicketDTO> result = ticketService.getAll();

        // Then
        // ✅ CORRECTION : Le service retourne une liste vide, pas d'exception !
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        verify(ticketRepository, times(1)).findAll();
    }

    // ==================== TESTS DE SUPPRESSION (delete) ====================

    @Test
    @DisplayName("Test 14 : Supprimer un ticket avec succès")
    void testDelete_Success() {
        // Given
        when(ticketRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ticketRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> ticketService.delete(1L));

        // Then
        verify(ticketRepository, times(1)).existsById(1L);
        verify(ticketRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test 15 : Supprimer un ticket - ID inexistant (Erreur)")
    void testDelete_NotFound() {
        // Given
        when(ticketRepository.existsById(999L)).thenReturn(false);

        // When & Then
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> ticketService.delete(999L)
        );

        assertEquals("Aucun ticket trouvé avec l'ID : 999", exception.getMessage());
        assertEquals(BusinessErrorCodes.TICKET_NOT_FOUND, exception.getBusinessErrorCodes());

        verify(ticketRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Test 16 : Supprimer un ticket - ID null (Erreur)")
    void testDelete_NullId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.delete(null)
        );

        assertEquals("Invalid ID: null", exception.getMessage());
        verify(ticketRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Test 17 : Supprimer un ticket - ID négatif (Erreur)")
    void testDelete_NegativeId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.delete(-1L)
        );

        assertEquals("Invalid ID: -1", exception.getMessage());
        verify(ticketRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Test 18 : Supprimer un ticket - ID zéro (Erreur)")
    void testDelete_ZeroId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.delete(0L)
        );

        assertEquals("Invalid ID: 0", exception.getMessage());
        verify(ticketRepository, never()).deleteById(any());
    }

    // ==================== TESTS DE VÉRIFICATION DE PLACE RÉSERVÉE (isPlaceReserved) ====================

    @Test
    @DisplayName("Test 19 : Vérifier si place est réservée - OUI")
    void testIsPlaceReserved_True() {
        // Given
        Long placeId = 25L;
        Long projectionId = 100L;

        when(ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(projectionId, placeId))
                .thenReturn(true);

        // When
        boolean result = ticketService.isPlaceReserved(placeId, projectionId);

        // Then
        assertTrue(result);
        verify(ticketRepository, times(1))
                .existsByProjectionIdAndPlaceIdAndReserveTrue(projectionId, placeId);
    }

    @Test
    @DisplayName("Test 20 : Vérifier si place est réservée - NON")
    void testIsPlaceReserved_False() {
        // Given
        Long placeId = 25L;
        Long projectionId = 100L;

        when(ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(projectionId, placeId))
                .thenReturn(false);

        // When
        boolean result = ticketService.isPlaceReserved(placeId, projectionId);

        // Then
        assertFalse(result);
        verify(ticketRepository, times(1))
                .existsByProjectionIdAndPlaceIdAndReserveTrue(projectionId, placeId);
    }

    // ==================== TESTS DE VALIDATION DES DONNÉES ====================

    @Test
    @DisplayName("Test 21 : Créer un ticket - Prix négatif (Validation)")
    void testSave_NegativePrice_ValidationFails() {
        // Given
        ticketDTO.setPrix(-10.0);

        doThrow(new RuntimeException("Le prix doit être positif"))
                .when(objectsValidator).validate(any(TicketDTO.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> ticketService.save(ticketDTO));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Test 22 : Créer un ticket - Nom client null (Validation)")
    void testSave_NullClientName_ValidationFails() {
        // Given
        ticketDTO.setNomClient(null);

        doThrow(new RuntimeException("Le nom du client est obligatoire"))
                .when(objectsValidator).validate(any(TicketDTO.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> ticketService.save(ticketDTO));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Test 23 : Créer un ticket - Code paiement null (Validation)")
    void testSave_NullPaymentCode_ValidationFails() {
        // Given
        ticketDTO.setCodePaiement(null);

        doThrow(new RuntimeException("Le code de paiement est obligatoire"))
                .when(objectsValidator).validate(any(TicketDTO.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> ticketService.save(ticketDTO));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    // ==================== TESTS DE CAS LIMITES ====================

    @Test
    @DisplayName("Test 24 : Créer un ticket avec prix zéro")
    void testSave_ZeroPrice_Success() {
        // Given
        ticketDTO.setPrix(0.0);
        ticket.setPrix(0.0);

        doNothing().when(objectsValidator).validate(any(TicketDTO.class));
        when(ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(anyLong(), anyLong()))
                .thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // When
        TicketDTO result = ticketService.save(ticketDTO);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getPrix());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Test 25 : Créer un ticket - Réservé true")
    void testSave_ReservedTrue_Success() {
        // Given
        ticketDTO.setReserve(true);
        ticket.setReserve(true);

        doNothing().when(objectsValidator).validate(any(TicketDTO.class));
        when(ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(anyLong(), anyLong()))
                .thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // When
        TicketDTO result = ticketService.save(ticketDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.isReserve());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
}