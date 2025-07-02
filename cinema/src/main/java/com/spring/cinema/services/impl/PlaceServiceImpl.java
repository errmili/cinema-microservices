package com.spring.cinema.services.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.cinema.clients.projection.ProjectionClient;
import com.spring.cinema.clients.projection.ProjectionResponse;
import com.spring.cinema.clients.ticket.TicketClient;
import com.spring.cinema.dto.PlaceDTO;
import com.spring.cinema.exception.BusinessException;
import com.spring.cinema.exception.InvalidEntityException;
import com.spring.cinema.handler.BusinessErrorCodes;
import com.spring.cinema.models.Place;
import com.spring.cinema.models.Salle;
import com.spring.cinema.repository.PlaceRepository;

import com.spring.cinema.repository.SaleRepository;
import com.spring.cinema.services.PlaceService;
import com.spring.cinema.validators.ObjectsValidator;



@Service
@Slf4j
public class PlaceServiceImpl implements PlaceService {
    private PlaceRepository placeRepository;

    private SaleRepository saleRepository;

    private final ObjectsValidator<PlaceDTO> objectsValidator;

    private final TicketClient ticketClient;

    private final ProjectionClient projectionClient;

    @Autowired
    public PlaceServiceImpl(PlaceRepository placeRepository,
                            SaleRepository saleRepository,
                            ObjectsValidator<PlaceDTO> objectsValidator,
                            TicketClient ticketClient,
                            ProjectionClient projectionClient) {
        this.placeRepository = placeRepository;
        this.saleRepository = saleRepository;
        this.objectsValidator = objectsValidator;
        this.ticketClient = ticketClient;
        this.projectionClient = projectionClient;
    }

    @Override
    @Transactional
    public PlaceDTO save(PlaceDTO dto) {
        // Valider l'objet avant de le persister
        objectsValidator.validate(dto);

        try {
            Place place;

            // Si l'ID est présent, c'est une modification
            if (dto.getId() != null && dto.getId() > 0) {
                // ⭐ RÉCUPÉRER l'entité existante pour préserver les dates
                place = placeRepository.findById(dto.getId())
                        .orElseThrow(() -> new InvalidEntityException(
                                "Place non trouvée pour modification.",
                                BusinessErrorCodes.PLACE_NOT_FOUND
                        ));

                // ⭐ METTRE À JOUR seulement les champs modifiables
                place.setNumero(dto.getNumero());
                place.setLatitude(dto.getLatitude());
                place.setLongitude(dto.getLongitude());
                place.setAltitude(dto.getAltitude());
                place.setHeureDebut(dto.getHeureDebut());

                // Mettre à jour la salle si nécessaire
                if (!place.getSalle().getId().equals(dto.getSalleId())) {
                    Salle salle = saleRepository.findById(dto.getSalleId())
                            .orElseThrow(() -> new InvalidEntityException(
                                    "Salle non trouvée avec l'ID : " + dto.getSalleId(),
                                    BusinessErrorCodes.SALLE_NOT_FOUND
                            ));
                    place.setSalle(salle);
                }

                place = placeRepository.save(place);
                log.info("Place avec l'ID {} modifiée avec succès", dto.getId());
            } else {
                // ⭐ CRÉATION d'une nouvelle entité
                place = dto.toEntity(saleRepository);
                place = placeRepository.save(place);
                log.info("Nouvelle place créée avec l'ID {}", place.getId());
            }

            return PlaceDTO.fromEntity(place);

        } catch (InvalidEntityException e) {
            // Re-lancer l'exception si l'entité n'est pas trouvée
            throw e;
        } catch (Exception e) {
            // En cas d'échec, lancer une InvalidEntityException
            log.error("Erreur lors de la sauvegarde de la place : {}", e.getMessage(), e);
            throw new InvalidEntityException(
                    "Échec de la sauvegarde de la place.",
                    BusinessErrorCodes.PLACE_CREATION_FAILED
            );
        }
    }

    @Override
    public PlaceDTO getById(Long id) {
        validateId(id);
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException(
                        "Aucune place trouvée avec l'ID : " + id,
                        BusinessErrorCodes.PLACE_NOT_FOUND
                ));
        // Usage général sans statut réservation
        return PlaceDTO.fromEntity(place);
    }


    @Override
    public List<PlaceDTO> getAll() {
        List<Place> places = placeRepository.findAll();

        // ⭐ CORRECTION : Ne pas lancer d'erreur si vide
        if (places.isEmpty()) {
            log.info("Aucune place trouvée - retour liste vide");
            return new ArrayList<>();
        }

        return places.stream()
                .map(PlaceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        // Validation de l'ID
        validateId(id);

        if (!placeRepository.existsById(id)) {
            throw new InvalidEntityException(
                    "Aucune place trouvée avec l'ID : " + id,
                    BusinessErrorCodes.PLACE_NOT_FOUND
            );
        }

        placeRepository.deleteById(id);
    }


//    @Override
//    public List<PlaceDTO> getPlacesByProjectionId(Long projectionId) {
//        List<Place> places = placeRepository.findByProjectionId(projectionId);
//        if (places.isEmpty()) {
//            throw new InvalidEntityException(
//                    "Aucune place trouvée pour la projection : " + projectionId,
//                    BusinessErrorCodes.PLACE_NOT_FOUND
//            );
//        }
////        return places.stream()
////                .map(PlaceDTO::fromEntity)
////                .collect(Collectors.toList());
//
//        return places.stream()
//                .map(this::fromEntityWithReservation)  // <-- ici on utilise la méthode enrichie
//                .collect(Collectors.toList());
//    }

    @Override
    public List<PlaceDTO> getPlacesByProjectionId(Long idProjection) {
        ProjectionResponse projection = projectionClient.findProjectionByID(idProjection);
        //si il a pas retour optionnel on peut pas faire orElseThrow
//                .orElseThrow(() -> new InvalidEntityException(
//                        "Aucune place trouvée avec l'ID : " + idProjection
//                ));
        if (projection == null) {
            throw new InvalidEntityException(
                    "Aucune projection trouvée avec l'ID : " + idProjection
            );
        }
            List<Place> places = placeRepository.findBySalleId(projection.getSalleId());
            if (places.isEmpty()) {
                throw new InvalidEntityException(
                        "Aucune place trouvée pour la salle associée à la projection : " + idProjection,
                        BusinessErrorCodes.PLACE_NOT_FOUND
                );
            }

            return places.stream()
                    .map(place -> fromEntityWithReservation(place, idProjection))
                    .collect(Collectors.toList());

    }

        private PlaceDTO fromEntityWithReservation (Place place, Long projectionId){
            PlaceDTO dto = PlaceDTO.fromEntity(place);
            try {
                boolean reserved = ticketClient.isPlaceReserved(dto.getId(), projectionId);
                dto.setReserve(reserved);
            } catch (Exception e) {
                log.error("Erreur lors de la récupération du statut de réservation pour la place id {} et projection {} : {}", place.getId(), projectionId, e.getMessage());
                dto.setReserve(false); // Par défaut, non réservé si erreur
            }
            return dto;
        }

        // Méthode privée pour valider l'ID
        private void validateId (Long id){
            if (id == null || id <= 0) {
                throw new InvalidEntityException(
                        "ID de place invalide : " + id,
                        BusinessErrorCodes.INVALID_PLACE_ID
                );
            }
        }

//    private PlaceDTO fromEntityWithReservationFirst(Place place) {
//        PlaceDTO dto = PlaceDTO.fromEntity(place);
//        try {
//            boolean reserved = ticketClient.isPlaceReserved(place.getId());
//            dto.setReserve(reserved);
//        } catch (Exception e) {
//            log.error("Erreur lors de la récupération du statut de réservation pour la place id {} : {}", place.getId(), e.getMessage());
//            dto.setReserve(false); // Par défaut, non réservé si erreur
//        }
//        return dto;
//    }

}