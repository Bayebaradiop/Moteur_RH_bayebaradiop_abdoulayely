package com.company.hrsettlement.settlement.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Assiette transmise a l'administration fiscale.
 * <p>
 * Le moteur ne calcule pas l'impot : il se contente de repartir le brut entre la
 * part imposable et la part exoneree, puis delegue au port fiscal.
 *
 * @param montantImposable part soumise a l'impot
 * @param montantExonere  part exoneree (prime d'anciennete sous plafond)
 */
public record AssietteFiscale(BigDecimal montantImposable, BigDecimal montantExonere) {

	public AssietteFiscale {
		Objects.requireNonNull(montantImposable, "montantImposable est obligatoire");
		Objects.requireNonNull(montantExonere, "montantExonere est obligatoire");
	}
}
