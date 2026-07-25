package com.company.hrsettlement.settlement.domain.predicate;

import java.math.BigDecimal;
import java.util.function.Predicate;

/**
 * Regle de declenchement du controle de l'inspection du travail.
 */
public final class AuditPredicates {

	/** Seuil de net a partir duquel un audit devient obligatoire, en XOF. */
	private static final BigDecimal AUDIT_THRESHOLD = new BigDecimal("30000000");

	/** Le seuil doit etre depasse : un net exactement egal ne declenche pas d'audit. */
	public static final Predicate<BigDecimal> REQUIRES_AUDIT =
			netAmount -> netAmount.compareTo(AUDIT_THRESHOLD) > 0;

	private AuditPredicates() {
	}
}
