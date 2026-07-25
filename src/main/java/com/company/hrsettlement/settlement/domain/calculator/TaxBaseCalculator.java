package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.Money;
import com.company.hrsettlement.settlement.domain.model.TaxBase;

import java.math.BigDecimal;

/**
 * Repartit le montant brut entre part exoneree et part imposable.
 * <p>
 * Regle : la prime d'anciennete est exoneree jusqu'a 5 000 000 XOF ; au-dela, le
 * surplus rejoint l'assiette imposable. Le moteur ne calcule jamais l'impot
 * lui-meme : il se contente de preparer cette assiette pour le port fiscal.
 */
public class TaxBaseCalculator {

	/** Plafond d'exoneration de la prime d'anciennete, en XOF. */
	private static final BigDecimal SENIORITY_BONUS_EXEMPTION_CEILING = new BigDecimal("5000000");

	public TaxBase compute(BigDecimal grossAmount, BigDecimal seniorityBonus) {
		BigDecimal exemptAmount = seniorityBonus.min(SENIORITY_BONUS_EXEMPTION_CEILING);
		BigDecimal taxableAmount = grossAmount.subtract(exemptAmount).max(BigDecimal.ZERO);

		return new TaxBase(Money.scaled(taxableAmount), Money.scaled(exemptAmount));
	}
}
