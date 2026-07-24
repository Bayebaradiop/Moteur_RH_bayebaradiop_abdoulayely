package com.company.hrsettlement.settlement.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Informations de depart d'un employe : donnee d'entree du moteur de calcul.
 * <p>
 * Record immuable : une fois construit, l'objet ne peut plus etre altere par un
 * calculateur. Les dates arrivent de l'exterieur, le domaine ne lit jamais
 * l'horloge : chaque calcul est donc reproductible.
 *
 * @param employeeId         identifiant de l'employe
 * @param hireDate           date d'embauche
 * @param departureDate      date de depart
 * @param departureReason    motif du depart
 * @param baseSalary         salaire mensuel de base, en XOF
 * @param remainingLeaveDays nombre de jours de conges non pris
 * @param noticeRespected    indique si le preavis a ete respecte
 */
public record EmployeeDeparture(
		String employeeId,
		LocalDate hireDate,
		LocalDate departureDate,
		DepartureReason departureReason,
		BigDecimal baseSalary,
		int remainingLeaveDays,
		boolean noticeRespected) {

	/**
	 * Garantit l'absence de valeur nulle : les regles metier de coherence
	 * (dates, montants) sont, elles, portees par le validateur du domaine.
	 */
	public EmployeeDeparture {
		Objects.requireNonNull(employeeId, "employeeId est obligatoire");
		Objects.requireNonNull(hireDate, "hireDate est obligatoire");
		Objects.requireNonNull(departureDate, "departureDate est obligatoire");
		Objects.requireNonNull(departureReason, "departureReason est obligatoire");
		Objects.requireNonNull(baseSalary, "baseSalary est obligatoire");
	}
}
