package com.company.hrsettlement.settlement.domain.exception;

/** Signale une incoherence entre la date d'embauche et la date de depart. */
public class ExceptionDatesInvalides extends ExceptionMetier {

	public ExceptionDatesInvalides(String message) {
		super(message);
	}
}
