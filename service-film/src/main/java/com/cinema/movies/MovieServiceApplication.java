package com.cinema.movies;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.cinema.movies.models")           // ← Ajoutez cette ligne !
@EnableJpaRepositories("com.cinema.movies.repository")
@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients(basePackages = "com.cinema.booking")
@EnableDiscoveryClient
//@OpenAPIDefinition(
//		info = @Info(
//				title = "Movie Service API",
//				version = "1.0",
//				description = "OpenAPI Documentation for the Movie Service",
//				contact = @Contact(name = "Support", email = "support@movieservice.com"),
//				license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")
//		),
//		security = @SecurityRequirement(name = "bearerAuth")
//)
//@SecurityScheme(
//		name = "bearerAuth",
//		type = SecuritySchemeType.HTTP,
//		scheme = "bearer",
//		bearerFormat = "JWT",
//		in = SecuritySchemeIn.HEADER
//)
public class MovieServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieServiceApplication.class, args);
	}

}
