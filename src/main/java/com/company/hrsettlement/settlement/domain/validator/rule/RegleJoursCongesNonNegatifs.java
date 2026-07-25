package com.company.hrsettlement.settlement.domain.validator.rule;

import com.company.hrsettlement.settlement.domain.exception.ExceptionDepartInvalide;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.validator.RegleDepart;

/**
 * Verifie que le solde de conges n'est pas negatif : un solde negatif
 * transformerait une indemnite en retenue silencieuse.
 */
public class RegleJoursCongesNonNegatifs implements RegleDepart {

	@Override
	public void verifier(DepartEmploye depart) {
		if (depart.joursCongesRestants() < 0) {
			throw new ExceptionDepartInvalide(
					"Le nombre de jours de conges restants ne peut pas etre negatif");
		}
	}
}
