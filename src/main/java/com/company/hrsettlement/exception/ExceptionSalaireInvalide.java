package com.company.hrsettlement.exception;

/** Signale un salaire de base inexploitable par le calcul. */
public class ExceptionSalaireInvalide extends ExceptionMetier {

	public ExceptionSalaireInvalide(String message) {
		super(message);
	}
}
