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
public record SettlementResponse(

		@Schema(description = "Identifiant de l'employe", example = "EMP-001")
		String employeeId,

		@Schema(description = "Indemnite de conges non pris", example = "500000.00")
		BigDecimal leaveCompensation,

		@Schema(description = "Prime d'anciennete", example = "1312500.00")
		BigDecimal seniorityBonus,

		@Schema(description = "Penalite de preavis", example = "0.00")
		BigDecimal noticePenalty,

		@Schema(description = "Montant brut", example = "1812500.00")
		BigDecimal grossAmount,

		@Schema(description = "Impot du", example = "200000.00")
		BigDecimal taxAmount,

		@Schema(description = "Montant net verse", example = "1612500.00")
		BigDecimal netAmount,

		@Schema(description = "Audit de l'inspection du travail declenche", example = "false")
		boolean auditTriggered) {
}
