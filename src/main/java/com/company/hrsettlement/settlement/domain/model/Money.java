package com.company.hrsettlement.settlement.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regle d'arrondi monetaire unique de l'application.
 * <p>
 * Toute division precise explicitement une echelle et un mode d'arrondi. Les
 * centraliser ici evite que deux calculateurs n'arrondissent differemment, ce qui
 * ferait diverger le brut de la somme de ses composantes.
 */
public final class Money {

	/** Deux decimales : precision comptable retenue pour le solde de tout compte. */
	public static final int SCALE = 2;

	/** Arrondi commercial : l'arrondi au plus proche, en faveur du salarie a egalite. */
	public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

	public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE);

	private Money() {
	}

	/** Ramene un montant a l'echelle monetaire de reference. */
	public static BigDecimal scaled(BigDecimal amount) {
		return amount.setScale(SCALE, ROUNDING_MODE);
	}

	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		return dividend.divide(divisor, SCALE, ROUNDING_MODE);
	}

	public static BigDecimal multiply(BigDecimal amount, BigDecimal multiplier) {
		return scaled(amount.multiply(multiplier));
	}
}
