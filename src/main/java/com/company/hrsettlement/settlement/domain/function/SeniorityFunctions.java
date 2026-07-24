package com.company.hrsettlement.settlement.domain.function;

import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;

import java.time.temporal.ChronoUnit;
import java.util.function.Function;

/**
 * Transformations liees a l'anciennete d'un employe.
 */
public final class SeniorityFunctions {

	/** Nombre d'annees pleines separant l'embauche du depart. */
	public static final Function<EmployeeDeparture, Integer> YEARS_WORKED =
			departure -> (int) ChronoUnit.YEARS.between(departure.hireDate(), departure.departureDate());

	private SeniorityFunctions() {
	}
}
