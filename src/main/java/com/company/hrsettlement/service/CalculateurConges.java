package com.company.hrsettlement.service;

import com.company.hrsettlement.domain.DepartEmploye;
import com.company.hrsettlement.domain.Monnaie;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Calcule l'indemnite compensatrice de conges non pris.
 * <p>
 * Regle : valeur d'une journee de travail multipliee par le nombre de jours de
 * conges restants.
 */
@Component
public class CalculateurConges {

	/** Nombre de jours ouvrables retenus dans un mois. */
	private static final BigDecimal JOURS_OUVRABLES_PAR_MOIS = new BigDecimal("21");

	/**
	 * Valeur d'une journee de travail. Nommee une fois : le jour ou les jours
	 * ouvrables passent de 21 a 22, un seul point du code change.
	 */
	private static final Function<DepartEmploye, BigDecimal> SALAIRE_JOURNALIER =
			depart -> Monnaie.diviser(depart.salaireBase(), JOURS_OUVRABLES_PAR_MOIS);

	public BigDecimal calculer(DepartEmploye depart) {
		BigDecimal salaireJournalier = SALAIRE_JOURNALIER.apply(depart);
		BigDecimal joursRestants = BigDecimal.valueOf(depart.joursCongesRestants());

		return Monnaie.multiplier(salaireJournalier, joursRestants);
	}
}
