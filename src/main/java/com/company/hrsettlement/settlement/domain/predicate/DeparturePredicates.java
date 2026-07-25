package com.company.hrsettlement.settlement.domain.predicate;

import com.company.hrsettlement.settlement.domain.model.DepartureReason;
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

	/** Le preavis n'est exigible que d'un employe qui demissionne. */
	public static final Predicate<EmployeeDeparture> IS_RESIGNATION =
			departure -> departure.departureReason() == DepartureReason.RESIGNATION;

	public static final Predicate<EmployeeDeparture> NOTICE_NOT_RESPECTED =
			departure -> !departure.noticeRespected();

	/** Seule une demission sans preavis expose l'employe a la retenue d'un mois de salaire. */
	public static final Predicate<EmployeeDeparture> LIABLE_FOR_NOTICE_PENALTY =
			IS_RESIGNATION.and(NOTICE_NOT_RESPECTED);

	private DeparturePredicates() {
	}
}
