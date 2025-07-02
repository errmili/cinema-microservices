package com.cinema.movies.services.impl;


import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import com.cinema.movies.dto.ProjectionDTO;
import com.cinema.movies.exception.BusinessException;
import com.cinema.movies.exception.InvalidEntityException;
import com.cinema.movies.handler.BusinessErrorCodes;
import com.cinema.movies.models.Film;
import com.cinema.movies.models.Projection;
import com.cinema.movies.models.Seance;
import com.cinema.movies.repository.FilmRepository;
import com.cinema.movies.repository.ProjectionRepository;
import com.cinema.movies.repository.SeanceRepository;
import com.cinema.movies.services.ProjectionService;
import com.cinema.movies.validators.ObjectsValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectionServiceImpl implements ProjectionService {

    private final ProjectionRepository projectionRepository;
    private final SeanceRepository seanceRepository;
    private final FilmRepository filmRepository;
    private final ObjectsValidator<ProjectionDTO> objectsValidator;

    @Autowired
    public ProjectionServiceImpl(ProjectionRepository projectionRepository,
                                 SeanceRepository seanceRepository,
                                 FilmRepository filmRepository,
                                 ObjectsValidator<ProjectionDTO> objectsValidator) {
        this.projectionRepository = projectionRepository;
        this.seanceRepository = seanceRepository;
        this.filmRepository = filmRepository;
        this.objectsValidator = objectsValidator;
    }

    @Override
    @Transactional
    public ProjectionDTO createProjection(ProjectionDTO dto) {
        // Valider l'objet avant de le persister
        objectsValidator.validate(dto);

        try {
            Projection projection;

            // Si l'ID est présent, c'est une modification
            if (dto.getId() != null && dto.getId() > 0) {
                // ⭐ RÉCUPÉRER l'entité existante pour préserver les dates
                projection = projectionRepository.findById(dto.getId())
                        .orElseThrow(() -> new InvalidEntityException(
                                "Projection non trouvée pour modification.",
                                BusinessErrorCodes.PROJECTION_NOT_FOUND
                        ));

                // ⭐ METTRE À JOUR seulement les champs modifiables
                projection.setDate(dto.getDate());
                projection.setPrix(dto.getPrix());
                projection.setSalleId(dto.getSalleId());

                // Mettre à jour le film si nécessaire
                if (!projection.getFilm().getId().equals(dto.getFilmId())) {
                    Film film = filmRepository.findById(dto.getFilmId())
                            .orElseThrow(() -> new InvalidEntityException(
                                    "Film non trouvé avec l'ID : " + dto.getFilmId(),
                                    BusinessErrorCodes.FILM_NOT_FOUND
                            ));
                    projection.setFilm(film);
                }

                // Mettre à jour la séance si nécessaire
                if (!projection.getSeance().getId().equals(dto.getSeanceId())) {
                    Seance seance = seanceRepository.findById(dto.getSeanceId())
                            .orElseThrow(() -> new InvalidEntityException(
                                    "Seance non trouvée avec l'ID : " + dto.getSeanceId(),
                                    BusinessErrorCodes.SEANCE_NOT_FOUND
                            ));
                    projection.setSeance(seance);
                }

                projection = projectionRepository.save(projection);
                log.info("Projection avec l'ID {} modifiée avec succès", dto.getId());
            } else {
                // ⭐ CRÉATION d'une nouvelle entité
                projection = dto.toEntity(seanceRepository, filmRepository);
                projection = projectionRepository.save(projection);
                log.info("Nouvelle projection créée avec l'ID {}", projection.getId());
            }

            return ProjectionDTO.fromEntity(projection);

        } catch (InvalidEntityException e) {
            // Re-lancer l'exception si l'entité n'est pas trouvée
            throw e;
        } catch (Exception e) {
            // En cas d'échec, lancer une InvalidEntityException
            log.error("Erreur lors de la sauvegarde de la projection : {}", e.getMessage(), e);
            throw new InvalidEntityException(
                    "Échec de la sauvegarde de la projection.",
                    BusinessErrorCodes.PROJECTION_CREATION_FAILED
            );
        }
    }

    @Override
    public ProjectionDTO getProjectionById(Long id) {
        // Validation de l'ID
        validateId(id);

        return projectionRepository.findById(id)
                .map(ProjectionDTO::fromEntity)
                .orElseThrow(() -> new InvalidEntityException(
                        "Aucune projection trouvée avec l'ID : " + id,
                        BusinessErrorCodes.PROJECTION_NOT_FOUND
                ));
    }

    @Override
    public List<ProjectionDTO> getAllProjections() {
        List<Projection> projections = projectionRepository.findAll();

        // ⭐ CORRECTION : Ne pas lancer d'erreur si vide
        if (projections.isEmpty()) {
            log.info("Aucune projection trouvée - retour liste vide");
            return new ArrayList<>();
        }

        return projections.stream()
                .map(ProjectionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProjection(Long id) {
        // Validation de l'ID
        validateId(id);

        if (!projectionRepository.existsById(id)) {
            throw new InvalidEntityException(
                    "Aucune projection trouvée avec l'ID : " + id,
                    BusinessErrorCodes.PROJECTION_NOT_FOUND
            );
        }

        projectionRepository.deleteById(id);
    }

    // Méthode privée pour valider l'ID
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidEntityException(
                    "ID de projection invalide : " + id,
                    BusinessErrorCodes.INVALID_PROJECTION_ID
            );
        }
    }
}
