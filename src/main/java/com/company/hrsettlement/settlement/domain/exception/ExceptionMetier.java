package com.company.hrsettlement.settlement.domain.exception;

/**
 * Racine des erreurs metier du domaine.
 * <p>
 * Une seule racine permet a la couche API de traduire toutes les violations de
 * regles en une reponse HTTP uniforme, sans connaitre chaque cas particulier.
 */
public abstract class ExceptionMetier extends RuntimeException {

	protected ExceptionMetier(String message) {
		super(message);
	}
}
