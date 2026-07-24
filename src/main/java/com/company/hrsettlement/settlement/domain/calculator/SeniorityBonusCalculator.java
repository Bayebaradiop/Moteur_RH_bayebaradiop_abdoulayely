package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.function.SeniorityFunctions;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Money;

import java.math.BigDecimal;

/**
 * Calcule la prime d'anciennete due a l'employe.
 * <p>
 * Regle : 10 % du salaire mensuel par annee d'anciennete.
 */
public class SeniorityBonusCalculator {

	/** Taux applique a chacune des premieres annees d'anciennete. */
	private static final BigDecimal BASE_YEAR_RATE = new BigDecimal("0.10");

	public BigDecimal compute(EmployeeDeparture departure) {
		int yearsWorked = SeniorityFunctions.YEARS_WORKED.apply(departure);
		BigDecimal rate = BASE_YEAR_RATE.multiply(BigDecimal.valueOf(yearsWorked));

		return Money.multiply(departure.baseSalary(), rate);
	}
}
