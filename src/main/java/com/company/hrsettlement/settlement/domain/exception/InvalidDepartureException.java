package com.company.hrsettlement.settlement.domain.exception;

/** Signale un depart incompatible avec les regles RH. */
public class InvalidDepartureException extends BusinessException {

	public InvalidDepartureException(String message) {
		super(message);
	}
}
