package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.function.SalaryFunctions;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Money;

import java.math.BigDecimal;

/**
 * Calcule l'indemnite compensatrice de conges non pris.
 * <p>
 * Regle : valeur d'une journee de travail multipliee par le nombre de jours de
 * conges restants.
 */
public class LeaveCalculator {

	public BigDecimal compute(EmployeeDeparture departure) {
		BigDecimal dailySalary = SalaryFunctions.DAILY_SALARY.apply(departure);
		BigDecimal remainingDays = BigDecimal.valueOf(departure.remainingLeaveDays());

		return Money.multiply(dailySalary, remainingDays);
	}
}
