package com.company.hrsettlement.settlement.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadonnees de la documentation exposee par Swagger UI.
 * <p>
 * Elles refletent le contrat de reference place dans
 * {@code src/main/resources/openapi/settlement-api.yaml}, qui reste la source de
 * verite de l'API.
 */
@Configuration
public class OpenApiConfiguration {

	@Bean
	public OpenAPI hrSettlementOpenApi() {
		return new OpenAPI().info(new Info()
				.title("Moteur RH - Solde de tout compte")
				.version("1.0.0")
				.description("Calcul des indemnites, de l'impot et du net du solde de tout compte")
				.contact(new Contact().name("Equipe RH").email("rh@company.com")));
	}
}
