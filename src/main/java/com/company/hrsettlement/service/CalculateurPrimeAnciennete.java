package com.company.hrsettlement.service;

import com.company.hrsettlement.domain.DepartEmploye;
import com.company.hrsettlement.domain.Monnaie;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Predicate;

/**
 * Calcule la prime d'anciennete due a l'employe.
 * <p>
 * Regle : reservee a la retraite et au licenciement economique. 10 % du salaire
 * mensuel pour chacune des cinq premieres annees, puis 15 % par annee
 * supplementaire.
 */
@Component
public class CalculateurPrimeAnciennete {

	/** Nombre d'annees couvertes par le taux de base. */
	private static final int SEUIL_ANNEES_TAUX_BASE = 5;

	/** Taux applique a chacune des cinq premieres annees d'anciennete. */
	private static final BigDecimal TAUX_ANNEE_BASE = new BigDecimal("0.10");

	/** Taux applique a chaque annee au-dela du seuil. */
	private static final BigDecimal TAUX_ANNEE_SUPPLEMENTAIRE = new BigDecimal("0.15");

	private static final Predicate<DepartEmploye> ELIGIBLE = DepartEmploye::eligiblePrimeAnciennete;

	public BigDecimal calculer(DepartEmploye depart) {
		if (!ELIGIBLE.test(depart)) {
			return Monnaie.ZERO;
		}

		return Monnaie.multiplier(depart.salaireBase(), tauxPour(depart.anneesTravaillees()));
	}

	private BigDecimal tauxPour(int anneesTravaillees) {
		int anneesBase = Math.min(anneesTravaillees, SEUIL_ANNEES_TAUX_BASE);
		int anneesSupplementaires = Math.max(anneesTravaillees - SEUIL_ANNEES_TAUX_BASE, 0);

		return TAUX_ANNEE_BASE.multiply(BigDecimal.valueOf(anneesBase))
				.add(TAUX_ANNEE_SUPPLEMENTAIRE.multiply(BigDecimal.valueOf(anneesSupplementaires)));
	}
}
