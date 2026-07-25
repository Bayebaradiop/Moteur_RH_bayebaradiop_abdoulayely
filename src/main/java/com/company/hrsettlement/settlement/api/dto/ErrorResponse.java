package com.company.hrsettlement.settlement.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Format d'erreur uniforme de l'API.
 * <p>
 * Un format unique, quelle que soit l'origine de l'erreur : les clients n'ont
 * qu'une seule structure a interpreter.
 */
@Schema(description = "Erreur retournee par l'API")
public record ErrorResponse(
		LocalDateTime timestamp,
		int status,
		String error,
		String message,
		String path) {
}
