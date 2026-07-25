package com.company.hrsettlement.settlement.domain.validator.rule;

import com.company.hrsettlement.settlement.domain.exception.ExceptionDatesInvalides;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.validator.RegleDepart;

/**
 * Verifie que le depart survient apres l'embauche.
 * <p>
 * Sans cette regle, une anciennete negative produirait une prime negative.
 */
public class RegleCoherenceDates implements RegleDepart {

	@Override
	public void verifier(DepartEmploye depart) {
		if (!depart.dateDepart().isAfter(depart.dateEmbauche())) {
			throw new ExceptionDatesInvalides(
					"La date de depart doit etre posterieure a la date d'embauche");
		}
	}
}
