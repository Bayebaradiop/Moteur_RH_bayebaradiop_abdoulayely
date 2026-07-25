package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.function.FonctionsAnciennete;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.model.Monnaie;
import com.company.hrsettlement.settlement.domain.predicate.PredicatsDepart;

import java.math.BigDecimal;

/**
 * Calcule la prime d'anciennete due a l'employe.
 * <p>
 * Regle : reservee a la retraite et au licenciement economique. 10 % du salaire
 * mensuel pour chacune des cinq premieres annees, puis 15 % pour chaque annee
 * supplementaire.
 */
public class CalculateurPrimeAnciennete {

	/** Nombre d'annees couvertes par le taux de base. */
	private static final int SEUIL_ANNEES_TAUX_BASE = 5;

	/** Taux applique a chacune des cinq premieres annees d'anciennete. */
	private static final BigDecimal TAUX_ANNEE_BASE = new BigDecimal("0.10");

	/** Taux applique a chaque annee au-dela du seuil. */
	private static final BigDecimal TAUX_ANNEE_SUPPLEMENTAIRE = new BigDecimal("0.15");

	public BigDecimal calculer(DepartEmploye depart) {
		if (!PredicatsDepart.ELIGIBLE_PRIME_ANCIENNETE.test(depart)) {
			return Monnaie.ZERO;
		}

		int anneesTravaillees = FonctionsAnciennete.ANNEES_TRAVAILLEES.apply(depart);

		return Monnaie.multiplier(depart.salaireBase(), tauxPour(anneesTravaillees));
	}

	private BigDecimal tauxPour(int anneesTravaillees) {
		int anneesBase = Math.min(anneesTravaillees, SEUIL_ANNEES_TAUX_BASE);
		int anneesSupplementaires = Math.max(anneesTravaillees - SEUIL_ANNEES_TAUX_BASE, 0);

		return TAUX_ANNEE_BASE.multiply(BigDecimal.valueOf(anneesBase))
				.add(TAUX_ANNEE_SUPPLEMENTAIRE.multiply(BigDecimal.valueOf(anneesSupplementaires)));
	}
}
