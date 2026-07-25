package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.Money;

import java.math.BigDecimal;

/**
 * Consolide le montant brut du solde de tout compte.
 * <p>
 * Regle : indemnite de conges + prime d'anciennete - penalite de preavis. Le
 * resultat peut etre negatif : l'employe est alors redevable envers l'employeur.
 */
public class GrossCalculator {

	public BigDecimal compute(BigDecimal leaveCompensation, BigDecimal seniorityBonus, BigDecimal noticePenalty) {
		return Money.scaled(leaveCompensation
				.add(seniorityBonus)
				.subtract(noticePenalty));
	}
}
