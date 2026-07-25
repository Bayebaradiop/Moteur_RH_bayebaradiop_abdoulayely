package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Money;
import com.company.hrsettlement.settlement.domain.predicate.DeparturePredicates;

import java.math.BigDecimal;

/**
 * Calcule la penalite retenue lorsque le preavis n'est pas respecte.
 * <p>
 * Regle : uniquement en cas de demission, retenue d'un salaire mensuel. La
 * penalite est exprimee en valeur positive : c'est le calcul du brut qui la
 * soustrait, ce qui peut rendre le solde negatif.
 */
public class NoticePenaltyCalculator {

	public BigDecimal compute(EmployeeDeparture departure) {
		if (!DeparturePredicates.LIABLE_FOR_NOTICE_PENALTY.test(departure)) {
			return Money.ZERO;
		}

		return Money.scaled(departure.baseSalary());
	}
}
