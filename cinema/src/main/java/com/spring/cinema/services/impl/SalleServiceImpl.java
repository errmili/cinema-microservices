package com.spring.cinema.services.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.cinema.exception.BusinessException;
import com.spring.cinema.exception.InvalidEntityException;
import com.spring.cinema.handler.BusinessErrorCodes;
import com.spring.cinema.models.Cinema;
import com.spring.cinema.models.Salle;
import com.spring.cinema.repository.CinemaRepository;
import com.spring.cinema.repository.PlaceRepository;
import com.spring.cinema.repository.SaleRepository;

import com.spring.cinema.dto.SalleDTO;
import com.spring.cinema.services.SalleService;
import com.spring.cinema.validators.ObjectsValidator;

@Service
@Slf4j
public class SalleServiceImpl implements SalleService {

    private final SaleRepository salleRepository;

    private CinemaRepository cinemaRepository;

    private final PlaceRepository placeRepository;
    private final ObjectsValidator<SalleDTO> objectsValidator;

    @Autowired
    public SalleServiceImpl(SaleRepository salleRepository,
                            CinemaRepository cinemaRepository,
                            PlaceRepository placeRepository,  // Injecté
                            ObjectsValidator<SalleDTO> objectsValidator) {
        this.salleRepository = salleRepository;
        this.cinemaRepository = cinemaRepository;
        this.placeRepository = placeRepository;
        this.objectsValidator = objectsValidator;
    }

    @Override
    @Transactional
    public SalleDTO save(SalleDTO dto) {
        // Valider l'objet avant de le persister
        objectsValidator.validate(dto);

        try {
            Salle salle;

            // Si l'ID est présent, c'est une modification
            if (dto.getId() != null && dto.getId() > 0) {
                // ⭐ RÉCUPÉRER l'entité existante pour préserver les dates
                salle = salleRepository.findById(dto.getId())
                        .orElseThrow(() -> new InvalidEntityException(
                                "Salle non trouvée pour modification.",
                                BusinessErrorCodes.SALLE_NOT_FOUND
                        ));

                // ⭐ METTRE À JOUR seulement les champs modifiables
                salle.setName(dto.getName());

                // Mettre à jour le cinema si nécessaire
                if (!salle.getCinema().getId().equals(dto.getCinemaId())) {
                    Cinema cinema = cinemaRepository.findById(dto.getCinemaId())
                            .orElseThrow(() -> new InvalidEntityException(
                                    "Cinema non trouvé avec l'ID : " + dto.getCinemaId(),
                                    BusinessErrorCodes.CINEMA_NOT_FOUND
                            ));
                    salle.setCinema(cinema);
                }

                salle = salleRepository.save(salle);
                log.info("Salle avec l'ID {} modifiée avec succès", dto.getId());
            } else {
                // ⭐ CRÉATION d'une nouvelle entité
                salle = dto.toEntity(cinemaRepository);
                salle = salleRepository.save(salle);
                log.info("Nouvelle salle créée avec l'ID {}", salle.getId());
            }

            SalleDTO resultDto = SalleDTO.fromEntity(salle);

            // Compléter le nombre de places
            int count = placeRepository.countBySalleId(salle.getId());
            resultDto.setNombrePlaces(count);

            return resultDto;

        } catch (InvalidEntityException e) {
            // Re-lancer l'exception si l'entité n'est pas trouvée
            throw e;
        } catch (Exception e) {
            // En cas d'échec, lancer une InvalidEntityException
            log.error("Erreur lors de la sauvegarde de la salle : {}", e.getMessage(), e);
            throw new InvalidEntityException(
                    "Échec de la sauvegarde de la salle.",
                    BusinessErrorCodes.SALLE_CREATION_FAILED
            );
        }
    }

    @Override
    public SalleDTO getById(Long id) {
        // Validation de l'ID
        validateId(id);

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException(
                        "Aucune salle trouvée avec l'ID : " + id,
                        BusinessErrorCodes.SALLE_NOT_FOUND
                ));

        SalleDTO dto = SalleDTO.fromEntity(salle);

        // Compléter nombrePlaces via la méthode countBySalleId
        int count = placeRepository.countBySalleId(id);
        dto.setNombrePlaces(count);

        return dto;
    }

    @Override
    public List<SalleDTO> getAll() {
        List<Salle> salles = salleRepository.findAll();

        // ⭐ CORRECTION : Ne pas lancer d'erreur si vide
        if (salles.isEmpty()) {
            log.info("Aucune salle trouvée - retour liste vide");
            return new ArrayList<>();
        }

        return salles.stream().map(salle -> {
            SalleDTO dto = SalleDTO.fromEntity(salle);
            int count = placeRepository.countBySalleId(salle.getId());
            dto.setNombrePlaces(count);
            return dto;
        }).collect(Collectors.toList());
    }
    @Override
    public void delete(Long id) {
        // Validation de l'ID
        validateId(id);

        if (!salleRepository.existsById(id)) {
            throw new InvalidEntityException(
                    "Aucune salle trouvée avec l'ID : " + id,
                    BusinessErrorCodes.SALLE_NOT_FOUND
            );
        }

        salleRepository.deleteById(id);
    }

    // Méthode privée pour valider l'ID
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidEntityException(
                    "ID de salle invalide : " + id,
                    BusinessErrorCodes.INVALID_SALLE_ID
            );
        }
    }
}
