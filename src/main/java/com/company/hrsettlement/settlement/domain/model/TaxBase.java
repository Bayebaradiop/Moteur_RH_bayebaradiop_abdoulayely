package com.company.hrsettlement.settlement.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Assiette transmise a l'administration fiscale.
 * <p>
 * Le moteur ne calcule pas l'impot : il se contente de repartir le brut entre la
 * part imposable et la part exoneree, puis delegue au port fiscal.
 *
 * @param taxableAmount part soumise a l'impot
 * @param exemptAmount  part exoneree (prime d'anciennete sous plafond)
 */
public record TaxBase(BigDecimal taxableAmount, BigDecimal exemptAmount) {

	public TaxBase {
		Objects.requireNonNull(taxableAmount, "taxableAmount est obligatoire");
		Objects.requireNonNull(exemptAmount, "exemptAmount est obligatoire");
	}
}
