package com.company.hrsettlement.settlement.domain.model;

import java.math.BigDecimal;

/**
 * Resultat du calcul : le solde de tout compte d'un employe.
 * <p>
 * Le detail de chaque composante est conserve afin que la RH puisse justifier le
 * montant verse ligne par ligne.
 *
 * @param employeeId        identifiant de l'employe concerne
 * @param leaveCompensation indemnite de conges non pris
 * @param seniorityBonus    prime d'anciennete
 * @param noticePenalty     penalite de preavis, exprimee en valeur positive
 * @param grossAmount       montant brut, potentiellement negatif
 * @param taxAmount         impot calcule par l'administration fiscale
 * @param netAmount         montant net du solde de tout compte
 * @param auditTriggered    vrai si l'inspection du travail a ete notifiee
 */
public record Settlement(
		String employeeId,
		BigDecimal leaveCompensation,
		BigDecimal seniorityBonus,
		BigDecimal noticePenalty,
		BigDecimal grossAmount,
		BigDecimal taxAmount,
		BigDecimal netAmount,
		boolean auditTriggered) {
}
