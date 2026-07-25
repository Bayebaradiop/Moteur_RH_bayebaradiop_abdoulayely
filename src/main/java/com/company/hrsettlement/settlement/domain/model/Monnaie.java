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
public final class Monnaie {

	/** Deux decimales : precision comptable retenue pour le solde de tout compte. */
	public static final int ECHELLE = 2;

	/** Arrondi commercial : l'arrondi au plus proche, en faveur du salarie a egalite. */
	public static final RoundingMode MODE_ARRONDI = RoundingMode.HALF_UP;

	public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(ECHELLE, MODE_ARRONDI);

	private Monnaie() {
	}

	/** Ramene un montant a l'echelle monetaire de reference. */
	public static BigDecimal arrondir(BigDecimal montant) {
		return montant.setScale(ECHELLE, MODE_ARRONDI);
	}

	public static BigDecimal diviser(BigDecimal dividende, BigDecimal diviseur) {
		return dividende.divide(diviseur, ECHELLE, MODE_ARRONDI);
	}

	public static BigDecimal multiplier(BigDecimal montant, BigDecimal facteur) {
		return arrondir(montant.multiply(facteur));
	}
}
