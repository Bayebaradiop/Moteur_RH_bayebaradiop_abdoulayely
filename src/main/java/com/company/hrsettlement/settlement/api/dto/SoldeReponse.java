package com.company.hrsettlement.settlement.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Contrat de sortie de l'API.
 * <p>
 * Distinct du modele metier : la structure interne du domaine peut evoluer sans
 * casser les clients de l'API.
 */
@Schema(description = "Detail du solde de tout compte")
public record SoldeReponse(

		@Schema(description = "Identifiant de l'employe", example = "EMP-001")
		String matriculeEmploye,

		@Schema(description = "Indemnite de conges non pris", example = "500000.00")
		BigDecimal indemniteConges,

		@Schema(description = "Prime d'anciennete", example = "1312500.00")
		BigDecimal primeAnciennete,

		@Schema(description = "Penalite de preavis", example = "0.00")
		BigDecimal penalitePreavis,

		@Schema(description = "Montant brut", example = "1812500.00")
		BigDecimal montantBrut,

		@Schema(description = "Impot du", example = "200000.00")
		BigDecimal montantImpot,

		@Schema(description = "Montant net verse", example = "1612500.00")
		BigDecimal montantNet,

		@Schema(description = "Audit de l'inspection du travail declenche", example = "false")
		boolean auditDeclenche) {
}
