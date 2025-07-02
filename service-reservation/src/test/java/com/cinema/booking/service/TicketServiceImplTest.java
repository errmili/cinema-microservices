package com.cinema.booking.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cinema.booking.application.dto.TicketDTO;
import com.cinema.booking.application.exception.InvalidEntityException;
import com.cinema.booking.application.exception.ObjectValidationException;
import com.cinema.booking.domain.models.Ticket;
import com.cinema.booking.domain.repository.TicketRepository;
import com.cinema.booking.application.usecase.TicketServiceImpl;
import com.cinema.booking.handler.BusinessErrorCodes;
import com.cinema.booking.infrastructure.validators.ObjectsValidator;


// Annotation pour intégrer Mockito avec JUnit 5
@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ObjectsValidator<TicketDTO> objectsValidator;

    @InjectMocks
    private TicketServiceImpl ticketService;

    // Méthode utilitaire pour créer un TicketDTO valide
    private TicketDTO createValidTicketDTO() {
        return TicketDTO.builder()
                .nomClient("John Doe")        // Initialiser nomClient
                .codePaiement("ABC123")      // Initialiser codePaiement
                .prix(10.0)                  // Initialiser prix
                .projectionId(104L)          // Initialiser projectionId
                .placeId(4L)                 // Initialiser placeId
                .build();
    }

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this); // Initialise les mocks avant chaque test
//    }


    // first service
    @Test
    void testSaveValidTicket() {
        // Arrange
        TicketDTO ticketDTO = createValidTicketDTO();

        Ticket ticketEntity = new Ticket();
        ticketEntity.setPrix(10.0);  // Assurez-vous que le Ticket créé a également une valeur pour prix
        ticketEntity.setNomClient("John Doe");
        ticketEntity.setCodePaiement("ABC123");
        ticketEntity.setProjectionId(104L);
        ticketEntity.setPlaceId(4L);

        // Nous vérifions que la méthode validate est appelée
        doNothing().when(objectsValidator).validate(ticketDTO); // Ne fait rien, simulateur valide

        when(ticketRepository.existsByPlaceIdAndReserveTrue(ticketDTO.getPlaceId())).thenReturn(false);

        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketEntity); // Simuler la sauvegarde

        // Act
        TicketDTO savedTicket = ticketService.save(ticketDTO);

        // Assert
        assertNotNull(savedTicket); // Vérifie que le ticket sauvegardé n'est pas null
        assertEquals("John Doe", savedTicket.getNomClient());
        verify(objectsValidator, times(1)).validate(ticketDTO); // Vérifie que validate a été appelé une fois
        verify(ticketRepository, times(1)).existsByPlaceIdAndReserveTrue(ticketDTO.getPlaceId());
        verify(ticketRepository, times(1)).save(any(Ticket.class)); // Vérifie que save a été appelé une fois
    }

    @Test
    void testSaveWhenPlaceAlreadyReservedThrowsException() {
        // Arrange
        TicketDTO ticketDTO = createValidTicketDTO();
        when(ticketRepository.existsByPlaceIdAndReserveTrue(ticketDTO.getPlaceId())).thenReturn(true);
        doNothing().when(objectsValidator).validate(any()); // Permettre l'appel

        // Act & Assert
        InvalidEntityException thrown = assertThrows(
                InvalidEntityException.class,
                () -> ticketService.save(ticketDTO)
        );

        assertEquals("Cette place est déjà réservée.", thrown.getMessage());
        verify(ticketRepository, never()).save(any());
    }


    @Test
    void testSaveWhenValidationFailsThrowsException() {
        // Arrange
        TicketDTO ticketDTO = createValidTicketDTO();
        doThrow(new InvalidEntityException("Validation failed", BusinessErrorCodes.TICKET_CREATION_FAILED))
                .when(objectsValidator).validate(ticketDTO);

        // Act & Assert
        InvalidEntityException thrown = assertThrows(
                InvalidEntityException.class,
                () -> ticketService.save(ticketDTO)
        );

        assertEquals("Validation failed", thrown.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void testSaveWhenRepositoryThrowsException() {
        // Arrange
        TicketDTO ticketDTO = createValidTicketDTO();
        doNothing().when(objectsValidator).validate(ticketDTO);
        when(ticketRepository.existsByPlaceIdAndReserveTrue(ticketDTO.getPlaceId())).thenReturn(false);
        when(ticketRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        InvalidEntityException thrown = assertThrows(
                InvalidEntityException.class,
                () -> ticketService.save(ticketDTO)
        );

        assertEquals("Échec de la création du ticket.", thrown.getMessage());
    }


    // seconde service
    @Test
    void testGetByIdNotFound() {
        // Arrange
        Long ticketId = 1L;
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // Act & Assert
        InvalidEntityException thrown = assertThrows(
                InvalidEntityException.class,
                () -> ticketService.getById(ticketId)
        );
        assertEquals("Aucun ticket trouvé avec l'ID : " + ticketId, thrown.getMessage());

        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @Test
    void testGetByIdSuccess() {
        // Arrange
        Long ticketId = 1L;
        Ticket ticket = new Ticket();
        // Remplis les données nécessaires dans ticket si besoin
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act
        TicketDTO foundTicket = ticketService.getById(ticketId);

        // Assert
        assertNotNull(foundTicket);
        verify(ticketRepository, times(1)).findById(ticketId);
    }


    @Test
    void testGetAllTicketsEmpty() {
        // Arrange
        when(ticketRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        InvalidEntityException thrown = assertThrows(
                InvalidEntityException.class,
                () -> ticketService.getAll()
        );
        assertEquals("Aucun ticket trouvé dans la base de données.", thrown.getMessage());

        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTicketsSuccess() {
        // Arrange
        Ticket ticket = new Ticket();
        // Remplir ticket si besoin
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket));

        // Act
        List<TicketDTO> tickets = ticketService.getAll();

        // Assert
        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void testDeleteTicketNotFound() {
        // Arrange
        Long ticketId = 1L;
        when(ticketRepository.existsById(ticketId)).thenReturn(false);

        // Act & Assert
        InvalidEntityException thrown = assertThrows(
                InvalidEntityException.class,
                () -> ticketService.delete(ticketId)
        );
        assertEquals("Aucun ticket trouvé avec l'ID : " + ticketId, thrown.getMessage());

        verify(ticketRepository, times(1)).existsById(ticketId);
        verify(ticketRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteTicketSuccess() {
        // Arrange
        Long ticketId = 1L;
        when(ticketRepository.existsById(ticketId)).thenReturn(true);

        // Act
        ticketService.delete(ticketId);

        // Assert
        verify(ticketRepository, times(1)).existsById(ticketId);
        verify(ticketRepository, times(1)).deleteById(ticketId);
    }


    @Test
    void testIsPlaceReservedTrue() {
        Long placeId = 1L;
        Long projectionId = 2L;

        when(ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(projectionId, placeId))
                .thenReturn(true);

        boolean result = ticketService.isPlaceReserved(placeId, projectionId);

        assertTrue(result);
        verify(ticketRepository, times(1))
                .existsByProjectionIdAndPlaceIdAndReserveTrue(projectionId, placeId);
    }

    @Test
    void testIsPlaceReservedFalse() {
        Long placeId = 1L;
        Long projectionId = 2L;

        when(ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(projectionId, placeId))
                .thenReturn(false);

        boolean result = ticketService.isPlaceReserved(placeId, projectionId);

        assertFalse(result);
        verify(ticketRepository, times(1))
                .existsByProjectionIdAndPlaceIdAndReserveTrue(projectionId, placeId);
    }


    @Test
    void testGetByIdWithInvalidId() {
        Long invalidId = 0L;

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.getById(invalidId)
        );

        assertEquals("Invalid ID: " + invalidId, thrown.getMessage());
    }

}

