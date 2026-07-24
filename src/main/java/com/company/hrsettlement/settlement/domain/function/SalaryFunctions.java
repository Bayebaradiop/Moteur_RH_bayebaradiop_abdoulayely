package com.company.hrsettlement.settlement.domain.function;

import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Money;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Transformations metier reutilisables appliquees a un depart.
 * <p>
 * Nommer ces conversions une seule fois evite de repeter la formule dans plusieurs
 * calculateurs : le jour ou les jours ouvrables passent de 21 a 22, un seul point
 * du code change.
 */
public final class SalaryFunctions {

	/** Nombre de jours ouvrables retenus dans un mois. */
	private static final BigDecimal WORKING_DAYS_PER_MONTH = new BigDecimal("21");

	/** Valeur d'une journee de travail : salaire mensuel rapporte aux jours ouvrables. */
	public static final Function<EmployeeDeparture, BigDecimal> DAILY_SALARY =
			departure -> Money.divide(departure.baseSalary(), WORKING_DAYS_PER_MONTH);

	private SalaryFunctions() {
	}
}
