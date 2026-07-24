package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcule l'indemnite compensatrice de conges non pris.
 * <p>
 * Regle : valeur d'une journee = salaire mensuel / 21 jours ouvrables, puis
 * multiplication par le nombre de jours restants.
 */
public class LeaveCalculator {

	/** Nombre de jours ouvrables retenus dans un mois. */
	private static final BigDecimal WORKING_DAYS_PER_MONTH = new BigDecimal("21");

	private static final int MONETARY_SCALE = 2;

	public BigDecimal compute(EmployeeDeparture departure) {
		BigDecimal dailySalary = departure.baseSalary()
				.divide(WORKING_DAYS_PER_MONTH, MONETARY_SCALE, RoundingMode.HALF_UP);

		return dailySalary
				.multiply(BigDecimal.valueOf(departure.remainingLeaveDays()))
				.setScale(MONETARY_SCALE, RoundingMode.HALF_UP);
	}
}
