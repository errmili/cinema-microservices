package com.spring.cinema.clients.projection;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "movie-service",
        url = "${application.config.projection-url}"
        //       configuration = FeignInterceptorConfig.class
)
public interface ProjectionClient {

    @GetMapping("/{idProjection}")
    ProjectionResponse findProjectionByID(@PathVariable("idProjection") Long idProjection);

}
