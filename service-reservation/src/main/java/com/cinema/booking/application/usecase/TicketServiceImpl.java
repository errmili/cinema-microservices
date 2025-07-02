package com.cinema.booking.application.usecase;

import lombok.extern.slf4j.Slf4j;

import com.cinema.booking.application.dto.TicketDTO;
import com.cinema.booking.application.exception.InvalidEntityException;
import com.cinema.booking.handler.BusinessErrorCodes;
import com.cinema.booking.domain.models.Ticket;
import com.cinema.booking.domain.repository.TicketRepository;
import com.cinema.booking.domain.services.TicketService;
import com.cinema.booking.infrastructure.validators.ObjectsValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des tickets.
 * Il inclut des méthodes pour créer, obtenir, supprimer et lister les tickets.
 */
@Service
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final ObjectsValidator<TicketDTO> objectsValidator;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, ObjectsValidator<TicketDTO> objectsValidator) {
        this.ticketRepository = ticketRepository;
        this.objectsValidator = objectsValidator;
    }

    /**
     * Crée un ticket.
     *
     * @param dto Les données du ticket à créer.
     * @return Le ticket créé sous forme de DTO.
     * @throws InvalidEntityException Si la création échoue.
     */

    @Override
    @Transactional
    public TicketDTO save(TicketDTO dto) {
        // Valider l'objet avant de le persister
        objectsValidator.validate(dto);

        try {
            Ticket ticket;

            // Si l'ID est présent, c'est une modification
            if (dto.getId() != null && dto.getId() > 0) {
                // ⭐ RÉCUPÉRER l'entité existante
                ticket = ticketRepository.findById(dto.getId())
                        .orElseThrow(() -> new InvalidEntityException(
                                "Ticket non trouvé pour modification.",
                                BusinessErrorCodes.TICKET_NOT_FOUND
                        ));

                // ⭐ METTRE À JOUR seulement les champs modifiables
                ticket.setNomClient(dto.getNomClient());
                ticket.setCodePaiement(dto.getCodePaiement());
                ticket.setPrix(dto.getPrix());
                ticket.setProjectionId(dto.getProjectionId());
                ticket.setPlaceId(dto.getPlaceId());
                ticket.setReserve(dto.isReserve());

                ticket = ticketRepository.save(ticket);
                log.info("Ticket avec l'ID {} modifié avec succès", dto.getId());
            } else {
                // ⭐ CRÉATION d'une nouvelle entité - Vérifier que la place n'est pas déjà réservée
                boolean placeReservee = ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(
                        dto.getProjectionId(),
                        dto.getPlaceId()
                );
                if (placeReservee) {
                    throw new InvalidEntityException(
                            "Cette place est déjà réservée.",
                            BusinessErrorCodes.TICKET_CREATION_FAILED
                    );
                }

                ticket = dto.toEntity();
                ticket = ticketRepository.save(ticket);
                log.info("Nouveau ticket créé avec l'ID {}", ticket.getId());
            }

            return TicketDTO.fromEntity(ticket);

        } catch (InvalidEntityException e) {
            // Re-lancer l'exception si l'entité n'est pas trouvée
            throw e;
        } catch (Exception e) {
            // En cas d'échec, lancer une InvalidEntityException
            log.error("Erreur lors de la sauvegarde du ticket : {}", e.getMessage(), e);
            throw new InvalidEntityException(
                    "Échec de la sauvegarde du ticket.",
                    BusinessErrorCodes.TICKET_CREATION_FAILED
            );
        }
    }
//    @Override
//    public TicketDTO save(TicketDTO dto) {
//
//        // Valider l'objet avant de le persister
//        objectsValidator.validate(dto);
//
//        // Vérification que la place n'est pas déjà réservée
//        boolean placeReservee = ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(
//                dto.getProjectionId(),  // ✅ AJOUT : Vérifier pour cette projection spécifique
//                dto.getPlaceId()
//        );
//        if (placeReservee) {
//            throw new InvalidEntityException(
//                    "Cette place est déjà réservée.",
//                    BusinessErrorCodes.TICKET_CREATION_FAILED
//            );
//        }
//
//        try {
//            Ticket ticket = ticketRepository.save(dto.toEntity());
//            return TicketDTO.fromEntity(ticket);
//        } catch (Exception e) {
//            // En cas d'échec de la création du ticket, on lance une InvalidEntityException
//            log.error("Erreur lors de la création du ticket : {}", e.getMessage());
//            throw new InvalidEntityException(
//                    "Échec de la création du ticket.",
//                    BusinessErrorCodes.TICKET_CREATION_FAILED
//            );
//        }
//    }


    @Override
    public TicketDTO getById(Long id) {
        // Validation de l'ID
        validateId(id);

        return ticketRepository.findById(id)
                .map(TicketDTO::fromEntity)
                .orElseThrow(() -> new InvalidEntityException(
                        "Aucun ticket trouvé avec l'ID : " + id,
                        BusinessErrorCodes.TICKET_NOT_FOUND
                ));
    }

    @Override
    public List<TicketDTO> getAll() {
        List<Ticket> tickets = ticketRepository.findAll();

        // ⭐ CORRECTION : Ne pas lancer d'erreur si vide
        if (tickets.isEmpty()) {
            log.info("Aucun ticket trouvé - retour liste vide");
            return new ArrayList<>();
        }

        return tickets.stream()
                .map(TicketDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        // Validation de l'ID
        validateId(id);

        if (!ticketRepository.existsById(id)) {
            throw new InvalidEntityException(
                    "Aucun ticket trouvé avec l'ID : " + id,
                    BusinessErrorCodes.TICKET_NOT_FOUND
            );
        }

        ticketRepository.deleteById(id);
    }

    @Override
    public boolean isPlaceReserved(Long placeId, Long idProjection) {
        //return ticketRepository.existsByPlaceIdAndReserveTrue(placeId);
        // Exemple : vérifier en base ou via API REST
        return ticketRepository.existsByProjectionIdAndPlaceIdAndReserveTrue(idProjection, placeId);
    }

    /**
     * Valide un identifiant pour s'assurer qu'il est valide (non null et supérieur à 0).
     *
     * Cette méthode est utilisée pour éviter la duplication du code de validation de l'ID.
     *
     * @param id L'identifiant à valider.
     * @throws IllegalArgumentException Si l'ID est null ou inférieur ou égal à 0.
     */
    // evite duplication
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
    }

    /*
    // Méthode privée pour valider l'ID
        private void validateId(Long id) {
            if (id == null || id <= 0) {
                throw new InvalidEntityException(
                        "ID de catégorie invalide : " + id,
                        BusinessErrorCodes.INVALID_CATEGORY_ID
                );
            }
     */


    /*
    Gestion des Transactions Exemple avec Spring @Transactional :

     * Crée un ticket et réserve une place dans la salle.
     * @param dto Détails du ticket à créer.
     * @return Le ticket créé.

         @Transactional
        public TicketDTO createTicketAndReservePlace(TicketDTO dto) {
        // Étape 1 : Créer un ticket
        Ticket ticket = ticketRepository.save(dto.toEntity());

        // Étape 2 : Réserver une place
        Salle salle = salleRepository.findById(dto.getSalleId())
                                      .orElseThrow(() -> new InvalidEntityException("Salle non trouvée"));
        salle.reservePlace(); // Supposons qu'il y ait une méthode pour réserver la place

        // Retourner le ticket créé
        return TicketDTO.fromEntity(ticket);
    }

Pourquoi utiliser @Transactional :
Si une des étapes échoue (par exemple la réservation de la place échoue après la création du ticket), toutes
les modifications seront annulées pour garantir la cohérence des données.
     */
}