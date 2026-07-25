package com.company.hrsettlement.settlement.domain.function;

import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.model.Monnaie;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Transformations metier reutilisables appliquees a un depart.
 * <p>
 * Nommer ces conversions une seule fois evite de repeter la formule dans plusieurs
 * calculateurs : le jour ou les jours ouvrables passent de 21 a 22, un seul point
 * du code change.
 */
public final class FonctionsSalaire {

	/** Nombre de jours ouvrables retenus dans un mois. */
	private static final BigDecimal JOURS_OUVRABLES_PAR_MOIS = new BigDecimal("21");

	/** Valeur d'une journee de travail : salaire mensuel rapporte aux jours ouvrables. */
	public static final Function<DepartEmploye, BigDecimal> SALAIRE_JOURNALIER =
			depart -> Monnaie.diviser(depart.salaireBase(), JOURS_OUVRABLES_PAR_MOIS);

	private FonctionsSalaire() {
	}
}
