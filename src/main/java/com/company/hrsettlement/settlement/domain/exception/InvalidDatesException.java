package com.company.hrsettlement.settlement.domain.exception;

/** Signale une incoherence entre la date d'embauche et la date de depart. */
public class InvalidDatesException extends BusinessException {

	public InvalidDatesException(String message) {
		super(message);
	}
}
