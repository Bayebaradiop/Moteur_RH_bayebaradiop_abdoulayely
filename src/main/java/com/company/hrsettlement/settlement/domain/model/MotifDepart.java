package com.company.hrsettlement.settlement.domain.model;

/**
 * Motif du depart d'un employe.
 * <p>
 * Le motif conditionne l'eligibilite a la prime d'anciennete et l'application
 * de la penalite de preavis.
 */
public enum MotifDepart {

	/** Depart volontaire de l'employe. */
	DEMISSION,

	/** Depart a la retraite. */
	RETRAITE,

	/** Licenciement pour motif economique. */
	LICENCIEMENT_ECONOMIQUE,

	/** Licenciement pour faute grave. */
	FAUTE_GRAVE
}
