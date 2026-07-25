package com.company.hrsettlement.settlement.domain.validator.rule;

import com.company.hrsettlement.settlement.domain.exception.InvalidDepartureException;
import com.company.hrsettlement.settlement.domain.function.SeniorityFunctions;
import com.company.hrsettlement.settlement.domain.model.DepartureReason;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.validator.DepartureRule;

/**
 * Regle dependante du motif : un depart a la retraite suppose une anciennete
 * minimale d'une annee pleine chez l'employeur.
 */
public class RetirementSeniorityRule implements DepartureRule {

	private static final int MINIMUM_RETIREMENT_SENIORITY_YEARS = 1;

	@Override
	public void check(EmployeeDeparture departure) {
		if (departure.departureReason() != DepartureReason.RETIREMENT) {
			return;
		}

		if (SeniorityFunctions.YEARS_WORKED.apply(departure) < MINIMUM_RETIREMENT_SENIORITY_YEARS) {
			throw new InvalidDepartureException(
					"Un depart a la retraite exige au moins une annee d'anciennete");
		}
	}
}
