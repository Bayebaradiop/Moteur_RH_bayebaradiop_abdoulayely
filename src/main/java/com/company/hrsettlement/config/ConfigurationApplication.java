package com.company.hrsettlement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Beans techniques de l'application.
 * <p>
 * L'horloge est injectee plutot qu'appelee statiquement : un test peut la figer.
 * Les metadonnees OpenAPI refletent le contrat de reference place dans
 * {@code src/main/resources/openapi/solde-api.yaml}.
 */
@Configuration
public class ConfigurationApplication {

	@Bean
	public Clock horloge() {
		return Clock.systemDefaultZone();
	}

	@Bean
	public OpenAPI openApiMoteurRh() {
		return new OpenAPI().info(new Info()
				.title("Moteur RH - Solde de tout compte")
				.version("1.0.0")
				.description("Calcul des indemnites, de l'impot et du net du solde de tout compte")
				.contact(new Contact().name("Equipe RH").email("rh@company.com")));
	}
}
