package com.company.hrsettlement.exception;

/**
 * Racine des erreurs metier.
 * <p>
 * Une seule racine permet au gestionnaire global de traduire toutes les
 * violations de regles en une reponse HTTP uniforme.
 */
public abstract class ExceptionMetier extends RuntimeException {

	protected ExceptionMetier(String message) {
		super(message);
	}
}
