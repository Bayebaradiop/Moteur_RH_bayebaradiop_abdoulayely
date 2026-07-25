package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.model.Monnaie;
import com.company.hrsettlement.settlement.domain.predicate.PredicatsDepart;

import java.math.BigDecimal;

/**
 * Calcule la penalite retenue lorsque le preavis n'est pas respecte.
 * <p>
 * Regle : uniquement en cas de demission, retenue d'un salaire mensuel. La
 * penalite est exprimee en valeur positive : c'est le calcul du brut qui la
 * soustrait, ce qui peut rendre le solde negatif.
 */
public class CalculateurPenalitePreavis {

	public BigDecimal calculer(DepartEmploye depart) {
		if (!PredicatsDepart.PENALITE_PREAVIS_APPLICABLE.test(depart)) {
			return Monnaie.ZERO;
		}

		return Monnaie.arrondir(depart.salaireBase());
	}
}
