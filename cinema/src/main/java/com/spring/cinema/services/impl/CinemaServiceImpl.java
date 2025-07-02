package com.spring.cinema.services.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.cinema.dto.CinemaDTO;
import com.spring.cinema.exception.BusinessException;
import com.spring.cinema.exception.InvalidEntityException;
import com.spring.cinema.handler.BusinessErrorCodes;
import com.spring.cinema.models.Cinema;
import com.spring.cinema.models.Ville;
import com.spring.cinema.repository.CinemaRepository;
import com.spring.cinema.repository.SaleRepository;
import com.spring.cinema.repository.VilleRepository;
import com.spring.cinema.services.CinemaService;
import com.spring.cinema.validators.ObjectsValidator;

@Service
@Slf4j
public class CinemaServiceImpl implements CinemaService {


    private CinemaRepository cinemaRepository;

    private VilleRepository villeRepository;

    private SaleRepository salleRepository;
    private final ObjectsValidator<CinemaDTO> objectsValidator;

    @Autowired
    public CinemaServiceImpl(
            CinemaRepository cinemaRepository,
            VilleRepository villeRepository,
            SaleRepository salleRepository,
            ObjectsValidator<CinemaDTO> objectsValidator
    ) {
        this.cinemaRepository = cinemaRepository;
        this.objectsValidator = objectsValidator;
        this.salleRepository = salleRepository;
        this.villeRepository = villeRepository;
    }

    @Override
    @Transactional
    public CinemaDTO save(CinemaDTO dto) {
        // Valider l'objet avant de le persister
        objectsValidator.validate(dto);

        try {
            Cinema cinema;

            // Si l'ID est présent, c'est une modification
            if (dto.getId() != null && dto.getId() > 0) {
                // ⭐ RÉCUPÉRER l'entité existante pour préserver les dates
                cinema = cinemaRepository.findById(dto.getId())
                        .orElseThrow(() -> new InvalidEntityException(
                                "Cinema non trouvé pour modification.",
                                BusinessErrorCodes.CINEMA_NOT_FOUND
                        ));

                // ⭐ METTRE À JOUR seulement les champs modifiables
                cinema.setName(dto.getName());
                cinema.setLatitude(dto.getLatitude());
                cinema.setLongitude(dto.getLongitude());
                cinema.setAltitude(dto.getAltitude());

                // Mettre à jour la ville si nécessaire
                if (!cinema.getVille().getId().equals(dto.getVilleId())) {
                    Ville ville = villeRepository.findById(dto.getVilleId())
                            .orElseThrow(() -> new InvalidEntityException(
                                    "Ville non trouvée avec l'ID : " + dto.getVilleId(),
                                    BusinessErrorCodes.VILLE_NOT_FOUND
                            ));
                    cinema.setVille(ville);
                }

                cinema = cinemaRepository.save(cinema);
                log.info("Cinema avec l'ID {} modifié avec succès", dto.getId());
            } else {
                // ⭐ CRÉATION d'une nouvelle entité
                cinema = dto.toEntity(villeRepository);
                cinema = cinemaRepository.save(cinema);
                log.info("Nouveau cinema créé avec l'ID {}", cinema.getId());
            }

            return CinemaDTO.fromEntity(cinema);

        } catch (InvalidEntityException e) {
            // Re-lancer l'exception si l'entité n'est pas trouvée
            throw e;
        } catch (Exception e) {
            // En cas d'échec, lancer une InvalidEntityException
            log.error("Erreur lors de la sauvegarde du cinema : {}", e.getMessage(), e);
            throw new InvalidEntityException(
                    "Échec de la sauvegarde du cinema.",
                    BusinessErrorCodes.CINEMA_CREATION_FAILED
            );
        }
    }

    @Override
    public CinemaDTO getById(Long id) {
        // Validation de l'ID
        validateId(id);

        return cinemaRepository.findById(id)
                .map(CinemaDTO::fromEntity)
                .orElseThrow(() -> new InvalidEntityException(
                        "Aucun cinéma trouvé avec l'ID : " + id,
                        BusinessErrorCodes.CINEMA_NOT_FOUND
                ));
    }

    @Override
    public List<CinemaDTO> getAll() {
        List<Cinema> cinemas = cinemaRepository.findAll();

        // ⭐ CORRECTION : Ne pas lancer d'erreur si vide
        if (cinemas.isEmpty()) {
            log.info("Aucun cinema trouvé - retour liste vide");
            return new ArrayList<>();
        }

        return cinemas.stream()
                .map(CinemaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        // Validation de l'ID
        validateId(id);

        if (!cinemaRepository.existsById(id)) {
            throw new InvalidEntityException(
                    "Aucun cinéma trouvé avec l'ID : " + id,
                    BusinessErrorCodes.CINEMA_NOT_FOUND
            );
        }

        cinemaRepository.deleteById(id);
    }


    public int getNombreSalles(Long cinemaId) {
        return salleRepository.countByCinemaId(cinemaId);
    }

    // Méthode privée pour valider l'ID
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidEntityException(
                    "ID de cinéma invalide : " + id,
                    BusinessErrorCodes.INVALID_CINEMA_ID
            );
        }
    }
}
