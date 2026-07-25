package com.company.hrsettlement.settlement.domain.function;

import com.company.hrsettlement.settlement.domain.model.DepartEmploye;

import java.time.temporal.ChronoUnit;
import java.util.function.Function;

/**
 * Transformations liees a l'anciennete d'un employe.
 */
public final class FonctionsAnciennete {

	/** Nombre d'annees pleines separant l'embauche du depart. */
	public static final Function<DepartEmploye, Integer> ANNEES_TRAVAILLEES =
			depart -> (int) ChronoUnit.YEARS.between(depart.dateEmbauche(), depart.dateDepart());

	private FonctionsAnciennete() {
	}
}
