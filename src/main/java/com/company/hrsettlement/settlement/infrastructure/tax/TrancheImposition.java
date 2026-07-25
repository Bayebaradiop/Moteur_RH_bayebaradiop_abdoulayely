package com.company.hrsettlement.settlement.infrastructure.tax;

import java.math.BigDecimal;

/**
 * Tranche du bareme fiscal.
 *
 * @param plafond plafond de la tranche, {@code null} pour la tranche superieure
 * @param taux       taux applique a la fraction du revenu comprise dans la tranche
 */
record TrancheImposition(BigDecimal plafond, BigDecimal taux) {

	/**
	 * Fraction du montant imposable qui tombe dans cette tranche.
	 *
	 * @param montantImposable montant imposable total
	 * @param plancher    plancher de la tranche
	 * @return la part imposable comprise entre le plancher et le plafond
	 */
	BigDecimal montantDansLaTranche(BigDecimal montantImposable, BigDecimal plancher) {
		BigDecimal plafondTranche = plafond == null ? montantImposable : montantImposable.min(plafond);

		return plafondTranche.subtract(plancher).max(BigDecimal.ZERO);
	}
}
