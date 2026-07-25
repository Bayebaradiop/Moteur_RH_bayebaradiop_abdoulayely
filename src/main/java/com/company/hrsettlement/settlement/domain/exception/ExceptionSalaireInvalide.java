package com.company.hrsettlement.settlement.domain.exception;

/** Signale un salaire de base inexploitable par le moteur de calcul. */
public class ExceptionSalaireInvalide extends ExceptionMetier {

	public ExceptionSalaireInvalide(String message) {
		super(message);
	}
}
