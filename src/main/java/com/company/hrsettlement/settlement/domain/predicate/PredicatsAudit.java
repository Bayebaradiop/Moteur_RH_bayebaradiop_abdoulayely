package com.company.hrsettlement.settlement.domain.predicate;

import java.math.BigDecimal;
import java.util.function.Predicate;

/**
 * Regle de declenchement du controle de l'inspection du travail.
 */
public final class PredicatsAudit {

	/** Seuil de net a partir duquel un audit devient obligatoire, en XOF. */
	private static final BigDecimal SEUIL_AUDIT = new BigDecimal("30000000");

	/** Le seuil doit etre depasse : un net exactement egal ne declenche pas d'audit. */
	public static final Predicate<BigDecimal> AUDIT_REQUIS =
			montantNet -> montantNet.compareTo(SEUIL_AUDIT) > 0;

	private PredicatsAudit() {
	}
}
