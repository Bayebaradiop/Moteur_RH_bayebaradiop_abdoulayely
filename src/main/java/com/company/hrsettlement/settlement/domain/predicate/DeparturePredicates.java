package com.company.hrsettlement.settlement.domain.predicate;

import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;

import java.util.function.Predicate;

/**
 * Regles booleennes du domaine, nommees une fois et reutilisables.
 * <p>
 * Le switch est volontairement exhaustif et sans branche par defaut : l'ajout d'un
 * nouveau motif de depart provoquera une erreur de compilation ici, forcant une
 * decision metier explicite plutot qu'un comportement implicite.
 */
public final class DeparturePredicates {

	/** Seuls la retraite et le licenciement economique ouvrent droit a la prime d'anciennete. */
	public static final Predicate<EmployeeDeparture> ELIGIBLE_FOR_SENIORITY_BONUS =
			departure -> switch (departure.departureReason()) {
				case RETIREMENT, ECONOMIC_DISMISSAL -> true;
				case RESIGNATION, SERIOUS_MISCONDUCT -> false;
			};

	private DeparturePredicates() {
	}
}
