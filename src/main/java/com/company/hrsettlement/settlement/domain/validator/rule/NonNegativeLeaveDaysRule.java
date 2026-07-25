package com.company.hrsettlement.settlement.domain.validator.rule;

import com.company.hrsettlement.settlement.domain.exception.InvalidDepartureException;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.validator.DepartureRule;

/**
 * Verifie que le solde de conges n'est pas negatif : un solde negatif
 * transformerait une indemnite en retenue silencieuse.
 */
public class NonNegativeLeaveDaysRule implements DepartureRule {

	@Override
	public void check(EmployeeDeparture departure) {
		if (departure.remainingLeaveDays() < 0) {
			throw new InvalidDepartureException(
					"Le nombre de jours de conges restants ne peut pas etre negatif");
		}
	}
}
