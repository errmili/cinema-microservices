package com.cinema.movies.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.movies.dto.SeanceDTO;
import com.cinema.movies.exception.BusinessException;
import com.cinema.movies.exception.InvalidEntityException;
import com.cinema.movies.handler.BusinessErrorCodes;
import com.cinema.movies.models.Seance;
import com.cinema.movies.repository.SeanceRepository;
import com.cinema.movies.services.SeanceService;
import com.cinema.movies.validators.ObjectsValidator;

@Service
@Slf4j
public class SeanceServiceImpl implements SeanceService {

    private final SeanceRepository seanceRepository;
    private final ObjectsValidator<SeanceDTO> objectsValidator;

    @Autowired
    public SeanceServiceImpl(SeanceRepository seanceRepository, ObjectsValidator<SeanceDTO> objectsValidator) {
        this.seanceRepository = seanceRepository;
        this.objectsValidator = objectsValidator;
    }

    @Override
    @Transactional
    public SeanceDTO createSeance(SeanceDTO dto) {
        // Valider l'objet avant de le persister
        objectsValidator.validate(dto);

        try {
            Seance seance;

            // Si l'ID est présent, c'est une modification
            if (dto.getId() != null && dto.getId() > 0) {
                // ⭐ RÉCUPÉRER l'entité existante pour préserver les dates
                seance = seanceRepository.findById(dto.getId())
                        .orElseThrow(() -> new InvalidEntityException(
                                "Seance non trouvée pour modification.",
                                BusinessErrorCodes.SEANCE_NOT_FOUND
                        ));

                // ⭐ METTRE À JOUR seulement les champs modifiables
                seance.setHeureDebut(dto.getHeureDebut());

                seance = seanceRepository.save(seance);
                log.info("Seance avec l'ID {} modifiée avec succès", dto.getId());
            } else {
                // ⭐ CRÉATION d'une nouvelle entité
                seance = dto.toEntity();
                seance = seanceRepository.save(seance);
                log.info("Nouvelle seance créée avec l'ID {}", seance.getId());
            }

            return SeanceDTO.fromEntity(seance);

        } catch (InvalidEntityException e) {
            // Re-lancer l'exception si l'entité n'est pas trouvée
            throw e;
        } catch (Exception e) {
            // En cas d'échec, lancer une InvalidEntityException
            log.error("Erreur lors de la sauvegarde de la seance : {}", e.getMessage(), e);
            throw new InvalidEntityException(
                    "Échec de la sauvegarde de la seance.",
                    BusinessErrorCodes.SEANCE_CREATION_FAILED
            );
        }
    }

    @Override
    public SeanceDTO getSeanceById(Long id) {
        // Validation de l'ID
        validateId(id);

        return seanceRepository.findById(id)
                .map(SeanceDTO::fromEntity)
                .orElseThrow(() -> new InvalidEntityException(
                        "Aucune séance trouvée avec l'ID : " + id,
                        BusinessErrorCodes.SEANCE_NOT_FOUND
                ));
    }

    @Override
    public List<SeanceDTO> getAllSeances() {
        List<Seance> seances = seanceRepository.findAll();

        // ⭐ CORRECTION : Ne pas lancer d'erreur si vide
        if (seances.isEmpty()) {
            log.info("Aucune séance trouvée - retour liste vide");
            return new ArrayList<>();
        }

        return seances.stream()
                .map(SeanceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSeance(Long id) {
        // Validation de l'ID
        validateId(id);

        if (!seanceRepository.existsById(id)) {
            throw new InvalidEntityException(
                    "Aucune séance trouvée avec l'ID : " + id,
                    BusinessErrorCodes.SEANCE_NOT_FOUND
            );
        }

        seanceRepository.deleteById(id);
    }

    // Méthode privée pour valider l'ID
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidEntityException(
                    "ID de séance invalide : " + id,
                    BusinessErrorCodes.INVALID_SEANCE_ID
            );
        }
    }
}
