package com.company.hrsettlement.settlement.domain.validator.rule;

import com.company.hrsettlement.settlement.domain.exception.InvalidSalaryException;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.validator.DepartureRule;

import java.math.BigDecimal;

/**
 * Verifie que le salaire de base est strictement positif : toutes les indemnites
 * en derivent.
 */
public class PositiveSalaryRule implements DepartureRule {

	@Override
	public void check(EmployeeDeparture departure) {
		if (departure.baseSalary().compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidSalaryException("Le salaire de base doit etre strictement positif");
		}
	}
}
