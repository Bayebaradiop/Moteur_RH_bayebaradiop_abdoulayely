package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.function.SeniorityFunctions;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Money;

import java.math.BigDecimal;

/**
 * Calcule la prime d'anciennete due a l'employe.
 * <p>
 * Regle : 10 % du salaire mensuel pour chacune des cinq premieres annees, puis
 * 15 % pour chaque annee supplementaire.
 */
public class SeniorityBonusCalculator {

	/** Nombre d'annees couvertes par le taux de base. */
	private static final int BASE_RATE_YEARS_LIMIT = 5;

	/** Taux applique a chacune des cinq premieres annees d'anciennete. */
	private static final BigDecimal BASE_YEAR_RATE = new BigDecimal("0.10");

	/** Taux applique a chaque annee au-dela du seuil. */
	private static final BigDecimal ADDITIONAL_YEAR_RATE = new BigDecimal("0.15");

	public BigDecimal compute(EmployeeDeparture departure) {
		int yearsWorked = SeniorityFunctions.YEARS_WORKED.apply(departure);

		return Money.multiply(departure.baseSalary(), rateFor(yearsWorked));
	}

	private BigDecimal rateFor(int yearsWorked) {
		int baseYears = Math.min(yearsWorked, BASE_RATE_YEARS_LIMIT);
		int additionalYears = Math.max(yearsWorked - BASE_RATE_YEARS_LIMIT, 0);

		return BASE_YEAR_RATE.multiply(BigDecimal.valueOf(baseYears))
				.add(ADDITIONAL_YEAR_RATE.multiply(BigDecimal.valueOf(additionalYears)));
	}
}
