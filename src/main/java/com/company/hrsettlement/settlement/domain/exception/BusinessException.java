package com.company.hrsettlement.settlement.domain.exception;

/**
 * Racine des erreurs metier du domaine.
 * <p>
 * Une seule racine permet a la couche API de traduire toutes les violations de
 * regles en une reponse HTTP uniforme, sans connaitre chaque cas particulier.
 */
public abstract class BusinessException extends RuntimeException {

	protected BusinessException(String message) {
		super(message);
	}
}
