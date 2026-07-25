package com.company.hrsettlement.settlement.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Horloge de l'application.
 * <p>
 * Elle est injectee plutot qu'appelee statiquement : un test peut la figer, et le
 * domaine, lui, n'en depend jamais.
 */
@Configuration
public class ConfigurationTemps {

	@Bean
	public Clock horloge() {
		return Clock.systemDefaultZone();
	}
}
