package com.company.hrsettlement.settlement.domain.exception;

/** Signale un depart incompatible avec les regles RH. */
public class ExceptionDepartInvalide extends ExceptionMetier {

	public ExceptionDepartInvalide(String message) {
		super(message);
	}
}
