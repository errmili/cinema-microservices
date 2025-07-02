package com.cinema.movies.services.impl;
import lombok.extern.slf4j.Slf4j;

import com.cinema.movies.dto.CategorieDTO;
import com.cinema.movies.exception.BusinessException;
import com.cinema.movies.exception.InvalidEntityException;
import com.cinema.movies.handler.BusinessErrorCodes;
import com.cinema.movies.models.Categorie;
import com.cinema.movies.repository.CategorieRepository;
import com.cinema.movies.services.CategorieService;
import com.cinema.movies.validators.ObjectsValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;
    private final ObjectsValidator<CategorieDTO> objectsValidator;

    @Autowired
    public CategorieServiceImpl(CategorieRepository categorieRepository, ObjectsValidator<CategorieDTO> objectsValidator) {
        this.categorieRepository = categorieRepository;
        this.objectsValidator = objectsValidator;
    }

    @Override
    @Transactional
    public CategorieDTO createCategorie(CategorieDTO dto) {
        // Valider l'objet avant de le persister
        objectsValidator.validate(dto);

        try {
            Categorie categorie;

            // Si l'ID est présent, c'est une modification
            if (dto.getId() != null && dto.getId() > 0) {
                // ⭐ RÉCUPÉRER l'entité existante pour préserver les dates
                categorie = categorieRepository.findById(dto.getId())
                        .orElseThrow(() -> new InvalidEntityException(
                                "Categorie non trouvée pour modification.",
                                BusinessErrorCodes.CATEGORY_NOT_FOUND
                        ));

                // ⭐ METTRE À JOUR seulement les champs modifiables
                categorie.setNom(dto.getNom());

                categorie = categorieRepository.save(categorie);
                log.info("Categorie avec l'ID {} modifiée avec succès", dto.getId());
            } else {
                // ⭐ CRÉATION d'une nouvelle entité
                categorie = dto.toEntity();
                categorie = categorieRepository.save(categorie);
                log.info("Nouvelle categorie créée avec l'ID {}", categorie.getId());
            }

            return CategorieDTO.fromEntity(categorie);

        } catch (InvalidEntityException e) {
            // Re-lancer l'exception si l'entité n'est pas trouvée
            throw e;
        } catch (Exception e) {
            // En cas d'échec, lancer une InvalidEntityException
            log.error("Erreur lors de la sauvegarde de la categorie : {}", e.getMessage(), e);
            throw new InvalidEntityException(
                    "Échec de la sauvegarde de la categorie.",
                    BusinessErrorCodes.CATEGORY_CREATION_FAILED
            );
        }
    }

    @Override
    public CategorieDTO getCategorieById(Long id) {
        // Validation de l'ID
        validateId(id);

        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException(
                        "Aucune catégorie trouvée avec l'ID : " + id,
                        BusinessErrorCodes.CATEGORY_NOT_FOUND
                ));
        return CategorieDTO.fromEntity(categorie);
    }

    @Override
    public List<CategorieDTO> getAllCategories() {
        List<Categorie> categories = categorieRepository.findAll();

        // ⭐ CORRECTION : Ne pas lancer d'erreur si vide
        if (categories.isEmpty()) {
            log.info("Aucune catégorie trouvée - retour liste vide");
            return new ArrayList<>();
        }

        return categories.stream()
                .map(CategorieDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategorie(Long id) {
        // Validation de l'ID
        validateId(id);

        if (!categorieRepository.existsById(id)) {
            throw new InvalidEntityException(
                    "Aucune catégorie trouvée avec l'ID : " + id,
                    BusinessErrorCodes.CATEGORY_NOT_FOUND
            );
        }

        categorieRepository.deleteById(id);
    }



    // Méthode privée pour valider l'ID
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidEntityException(
                    "ID de catégorie invalide : " + id,
                    BusinessErrorCodes.INVALID_CATEGORY_ID
            );
        }
    }
}