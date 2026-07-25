package com.company.hrsettlement.settlement.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Format d'erreur uniforme de l'API.
 * <p>
 * Un format unique, quelle que soit l'origine de l'erreur : les clients n'ont
 * qu'une seule structure a interpreter.
 *
 * @param horodatage date et heure de l'erreur
 * @param statut     code HTTP retourne
 * @param erreur     libelle standard du code HTTP
 * @param message    explication destinee au client
 * @param chemin     URL appelee
 */
@Schema(description = "Erreur retournee par l'API")
public record ErreurReponse(
		LocalDateTime horodatage,
		int statut,
		String erreur,
		String message,
		String chemin) {
}
