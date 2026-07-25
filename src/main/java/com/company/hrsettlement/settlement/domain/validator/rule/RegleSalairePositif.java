package com.company.hrsettlement.settlement.domain.validator.rule;

import com.company.hrsettlement.settlement.domain.exception.ExceptionSalaireInvalide;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.validator.RegleDepart;

import java.math.BigDecimal;

/**
 * Verifie que le salaire de base est strictement positif : toutes les indemnites
 * en derivent.
 */
public class RegleSalairePositif implements RegleDepart {

	@Override
	public void verifier(DepartEmploye depart) {
		if (depart.salaireBase().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ExceptionSalaireInvalide("Le salaire de base doit etre strictement positif");
		}
	}
}
