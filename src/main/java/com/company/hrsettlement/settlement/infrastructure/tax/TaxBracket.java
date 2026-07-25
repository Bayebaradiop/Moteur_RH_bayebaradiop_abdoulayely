package com.company.hrsettlement.settlement.infrastructure.tax;

import java.math.BigDecimal;

/**
 * Tranche du bareme fiscal.
 *
 * @param upperBound plafond de la tranche, {@code null} pour la tranche superieure
 * @param rate       taux applique a la fraction du revenu comprise dans la tranche
 */
record TaxBracket(BigDecimal upperBound, BigDecimal rate) {

	/**
	 * Fraction du montant imposable qui tombe dans cette tranche.
	 *
	 * @param taxableAmount montant imposable total
	 * @param lowerBound    plancher de la tranche
	 * @return la part imposable comprise entre le plancher et le plafond
	 */
	BigDecimal amountWithin(BigDecimal taxableAmount, BigDecimal lowerBound) {
		BigDecimal ceiling = upperBound == null ? taxableAmount : taxableAmount.min(upperBound);

		return ceiling.subtract(lowerBound).max(BigDecimal.ZERO);
	}
}
