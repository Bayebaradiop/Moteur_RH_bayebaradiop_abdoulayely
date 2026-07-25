package com.company.hrsettlement.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Assiette transmise a l'administration fiscale.
 * <p>
 * Le moteur ne calcule pas l'impot : il repartit le brut entre part imposable et
 * part exoneree, puis delegue.
 *
 * @param montantImposable part soumise a l'impot
 * @param montantExonere   part exoneree (prime d'anciennete sous plafond)
 */
public record AssietteFiscale(BigDecimal montantImposable, BigDecimal montantExonere) {

	public AssietteFiscale {
		Objects.requireNonNull(montantImposable, "montantImposable est obligatoire");
		Objects.requireNonNull(montantExonere, "montantExonere est obligatoire");
	}
}
