package com.company.hrsettlement.settlement.domain.validator.rule;

import com.company.hrsettlement.settlement.domain.exception.ExceptionDepartInvalide;
import com.company.hrsettlement.settlement.domain.function.FonctionsAnciennete;
import com.company.hrsettlement.settlement.domain.model.MotifDepart;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.validator.RegleDepart;

/**
 * Regle dependante du motif : un depart a la retraite suppose une anciennete
 * minimale d'une annee pleine chez l'employeur.
 */
public class RegleAncienneteRetraite implements RegleDepart {

	private static final int ANNEES_ANCIENNETE_MINIMALE_RETRAITE = 1;

	@Override
	public void verifier(DepartEmploye depart) {
		if (depart.motifDepart() != MotifDepart.RETRAITE) {
			return;
		}

		if (FonctionsAnciennete.ANNEES_TRAVAILLEES.apply(depart) < ANNEES_ANCIENNETE_MINIMALE_RETRAITE) {
			throw new ExceptionDepartInvalide(
					"Un depart a la retraite exige au moins une annee d'anciennete");
		}
	}
}
