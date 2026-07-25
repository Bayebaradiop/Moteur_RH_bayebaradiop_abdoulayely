package com.company.hrsettlement.settlement.domain.exception;

/** Signale un salaire de base inexploitable par le moteur de calcul. */
public class InvalidSalaryException extends BusinessException {

	public InvalidSalaryException(String message) {
		super(message);
	}
}
