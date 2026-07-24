package com.company.hrsettlement.settlement.domain.model;

/**
 * Motif du depart d'un employe.
 * <p>
 * Le motif conditionne l'eligibilite a la prime d'anciennete et l'application
 * de la penalite de preavis.
 */
public enum DepartureReason {

	/** Depart volontaire de l'employe. */
	RESIGNATION,

	/** Depart a la retraite. */
	RETIREMENT,

	/** Licenciement pour motif economique. */
	ECONOMIC_DISMISSAL,

	/** Licenciement pour faute grave. */
	SERIOUS_MISCONDUCT
}
