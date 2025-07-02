package com.cinema.movies.clients.cenima;


import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "cinema-service",
        url = "${application.config.places-url}"
)
public interface PlaceClient {

    @GetMapping("/projection/{projectionId}")
    List<PlaceDTO> getPlacesByProjectionId(@PathVariable Long projectionId);
}
