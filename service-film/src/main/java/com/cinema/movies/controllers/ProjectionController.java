package com.cinema.movies.controllers;

import com.cinema.movies.controllers.api.ProjectionApi;
import com.cinema.movies.dto.ProjectionDTO;
import com.cinema.movies.services.ProjectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectionController implements ProjectionApi {

    private final ProjectionService projectionService;

    @Autowired
    public ProjectionController(ProjectionService projectionService) {
        this.projectionService = projectionService;
    }

    @Override
    public ProjectionDTO save(@RequestBody ProjectionDTO projectionDTO) {
        return projectionService.createProjection(projectionDTO);
    }

    @Override
    public ProjectionDTO findById(@PathVariable("idProjection") Long idProjection) {
        return projectionService.getProjectionById(idProjection);
    }

    @Override
    public List<ProjectionDTO> findAll() {
        return projectionService.getAllProjections();
    }

    @Override
    public void delete(@PathVariable("idProjection") Long idProjection) {
        projectionService.deleteProjection(idProjection);
    }
}
