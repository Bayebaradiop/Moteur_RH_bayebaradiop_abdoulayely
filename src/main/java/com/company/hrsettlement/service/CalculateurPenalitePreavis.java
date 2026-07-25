package com.company.hrsettlement.service;

import com.company.hrsettlement.domain.DepartEmploye;
import com.company.hrsettlement.domain.Monnaie;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Predicate;

/**
 * Calcule la penalite retenue lorsque le preavis n'est pas respecte.
 * <p>
 * Regle : uniquement en cas de demission, retenue d'un salaire mensuel. La
 * penalite est exprimee en valeur positive : c'est le calcul du brut qui la
 * soustrait, ce qui peut rendre le solde negatif.
 */
@Component
public class CalculateurPenalitePreavis {

	private static final Predicate<DepartEmploye> PENALITE_APPLICABLE = DepartEmploye::estDemissionSansPreavis;

	public BigDecimal calculer(DepartEmploye depart) {
		if (!PENALITE_APPLICABLE.test(depart)) {
			return Monnaie.ZERO;
		}

		return Monnaie.arrondir(depart.salaireBase());
	}
}
