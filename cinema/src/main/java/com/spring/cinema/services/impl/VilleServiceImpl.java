package com.spring.cinema.services.impl;

import lombok.extern.slf4j.Slf4j;

import com.spring.cinema.dto.*;
import com.spring.cinema.exception.BusinessException;
import com.spring.cinema.exception.InvalidEntityException;
import com.spring.cinema.handler.BusinessErrorCodes;
import com.spring.cinema.models.*;

import com.spring.cinema.repository.VilleRepository;
import com.spring.cinema.services.*;
import com.spring.cinema.validators.ObjectsValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VilleServiceImpl implements VilleService {

    private final VilleRepository villeRepository;
    private final ObjectsValidator<VilleDTO> objectsValidator;

    @Autowired
    public VilleServiceImpl(VilleRepository villeRepository, ObjectsValidator<VilleDTO> objectsValidator) {
        this.villeRepository = villeRepository;
        this.objectsValidator = objectsValidator;
    }

    @Override
    @Transactional
    public VilleDTO save(VilleDTO dto) {
        // Valider l'objet avant de le persister
        objectsValidator.validate(dto);

        try {
            Ville ville;

            // Si l'ID est présent, c'est une modification
            if (dto.getId() != null && dto.getId() > 0) {
                // ⭐ RÉCUPÉRER l'entité existante pour préserver les dates
                ville = villeRepository.findById(dto.getId())
                        .orElseThrow(() -> new InvalidEntityException(
                                "Ville non trouvée pour modification.",
                                BusinessErrorCodes.VILLE_NOT_FOUND
                        ));

                // ⭐ METTRE À JOUR seulement les champs modifiables
                ville.setCityName(dto.getCityName());
                ville.setLatitude(dto.getLatitude());
                ville.setLongitude(dto.getLongitude());
                ville.setAltitude(dto.getAltitude());
                // Les dates creationDate et lastModifiedDate sont préservées/mises à jour automatiquement

                ville = villeRepository.save(ville);
                log.info("Ville avec l'ID {} modifiée avec succès", dto.getId());
            } else {
                // ⭐ CRÉATION d'une nouvelle entité
                ville = dto.toEntity();
                ville = villeRepository.save(ville);
                log.info("Nouvelle ville créée avec l'ID {}", ville.getId());
            }

            return VilleDTO.fromEntity(ville);

        } catch (InvalidEntityException e) {
            // Re-lancer l'exception si l'entité n'est pas trouvée
            throw e;
        } catch (Exception e) {
            // En cas d'échec, lancer une InvalidEntityException
            log.error("Erreur lors de la sauvegarde de la ville : {}", e.getMessage(), e);
            throw new InvalidEntityException(
                    "Échec de la sauvegarde de la ville.",
                    BusinessErrorCodes.VILLE_CREATION_FAILED
            );
        }
    }

    @Override
    public VilleDTO getById(Long id) {
        // Validation de l'ID
        validateId(id);

        return villeRepository.findById(id)
                .map(VilleDTO::fromEntity)
                .orElseThrow(() -> new InvalidEntityException(
                        "Aucune ville trouvée avec l'ID : " + id,
                        BusinessErrorCodes.VILLE_NOT_FOUND
                ));
    }

    @Override
    public List<VilleDTO> getAll() {
        List<Ville> villes = villeRepository.findAll();

        // ⭐ CORRECTION : Ne pas lancer d'erreur si vide
        if (villes.isEmpty()) {
            log.info("Aucune ville trouvée - retour liste vide");
            return new ArrayList<>();
        }

        return villes.stream()
                .map(VilleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        // Validation de l'ID
        validateId(id);

        if (!villeRepository.existsById(id)) {
            throw new InvalidEntityException(
                    "Aucune ville trouvée avec l'ID : " + id,
                    BusinessErrorCodes.VILLE_NOT_FOUND
            );
        }

        villeRepository.deleteById(id);
    }

    // Méthode privée pour valider l'ID
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidEntityException(
                    "ID de ville invalide : " + id,
                    BusinessErrorCodes.INVALID_VILLE_ID
            );
        }
    }
}

