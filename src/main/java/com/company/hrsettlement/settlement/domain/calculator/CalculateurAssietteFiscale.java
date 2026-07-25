package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.Monnaie;
import com.company.hrsettlement.settlement.domain.model.AssietteFiscale;

import java.math.BigDecimal;

/**
 * Repartit le montant brut entre part exoneree et part imposable.
 * <p>
 * Regle : la prime d'anciennete est exoneree jusqu'a 5 000 000 XOF ; au-dela, le
 * surplus rejoint l'assiette imposable. Le moteur ne calcule jamais l'impot
 * lui-meme : il se contente de preparer cette assiette pour le port fiscal.
 */
public class CalculateurAssietteFiscale {

	/** Plafond d'exoneration de la prime d'anciennete, en XOF. */
	private static final BigDecimal PLAFOND_EXONERATION_PRIME = new BigDecimal("5000000");

	public AssietteFiscale calculer(BigDecimal montantBrut, BigDecimal primeAnciennete) {
		BigDecimal montantExonere = primeAnciennete.min(PLAFOND_EXONERATION_PRIME);
		BigDecimal montantImposable = montantBrut.subtract(montantExonere).max(BigDecimal.ZERO);

		return new AssietteFiscale(Monnaie.arrondir(montantImposable), Monnaie.arrondir(montantExonere));
	}
}
