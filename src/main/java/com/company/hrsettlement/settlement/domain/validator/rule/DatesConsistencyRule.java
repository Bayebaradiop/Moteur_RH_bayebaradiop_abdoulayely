package com.company.hrsettlement.settlement.domain.validator.rule;

import com.company.hrsettlement.settlement.domain.exception.InvalidDatesException;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.validator.DepartureRule;

/**
 * Verifie que le depart survient apres l'embauche.
 * <p>
 * Sans cette regle, une anciennete negative produirait une prime negative.
 */
public class DatesConsistencyRule implements DepartureRule {

	@Override
	public void check(EmployeeDeparture departure) {
		if (!departure.departureDate().isAfter(departure.hireDate())) {
			throw new InvalidDatesException(
					"La date de depart doit etre posterieure a la date d'embauche");
		}
	}
}
