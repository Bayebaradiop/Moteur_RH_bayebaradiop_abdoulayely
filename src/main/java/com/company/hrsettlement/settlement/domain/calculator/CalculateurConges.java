package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.function.FonctionsSalaire;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.model.Monnaie;

import java.math.BigDecimal;

/**
 * Calcule l'indemnite compensatrice de conges non pris.
 * <p>
 * Regle : valeur d'une journee de travail multipliee par le nombre de jours de
 * conges restants.
 */
public class CalculateurConges {

	public BigDecimal calculer(DepartEmploye depart) {
		BigDecimal salaireJournalier = FonctionsSalaire.SALAIRE_JOURNALIER.apply(depart);
		BigDecimal joursRestants = BigDecimal.valueOf(depart.joursCongesRestants());

		return Monnaie.multiplier(salaireJournalier, joursRestants);
	}
}
