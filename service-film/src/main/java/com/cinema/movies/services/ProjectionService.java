package com.cinema.movies.services;

import java.util.List;

import com.cinema.movies.dto.ProjectionDTO;

public interface ProjectionService {

    ProjectionDTO createProjection(ProjectionDTO projectionDTO);

    ProjectionDTO getProjectionById(Long id);

    List<ProjectionDTO> getAllProjections();

    void deleteProjection(Long id);
}
