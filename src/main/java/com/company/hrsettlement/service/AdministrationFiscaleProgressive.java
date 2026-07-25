package com.company.hrsettlement.service;

import com.company.hrsettlement.domain.AssietteFiscale;
import com.company.hrsettlement.domain.Monnaie;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Bareme progressif applique a la part imposable.
 * <p>
 * Implementation de reference : la remplacer par un appel au service des impots
 * ne demanderait aucune modification du moteur, qui ne connait que l'interface
 * {@link AdministrationFiscale}.
 */
@Service
public class AdministrationFiscaleProgressive implements AdministrationFiscale {

	/**
	 * Tranche du bareme.
	 *
	 * @param plafond plafond de la tranche, {@code null} pour la tranche superieure
	 * @param taux    taux applique a la fraction du revenu comprise dans la tranche
	 */
	private record Tranche(BigDecimal plafond, BigDecimal taux) {

		/** Fraction du montant imposable qui tombe dans cette tranche. */
		BigDecimal montantDansLaTranche(BigDecimal montantImposable, BigDecimal plancher) {
			BigDecimal sommet = plafond == null ? montantImposable : montantImposable.min(plafond);

			return sommet.subtract(plancher).max(BigDecimal.ZERO);
		}
	}

	/** Tranches de la plus basse a la plus haute ; la derniere n'a pas de plafond. */
	private static final List<Tranche> TRANCHES = List.of(
			new Tranche(new BigDecimal("630000"), new BigDecimal("0.00")),
			new Tranche(new BigDecimal("1500000"), new BigDecimal("0.20")),
			new Tranche(new BigDecimal("4000000"), new BigDecimal("0.30")),
			new Tranche(new BigDecimal("8000000"), new BigDecimal("0.35")),
			new Tranche(null, new BigDecimal("0.40")));

	@Override
	public BigDecimal calculerImpot(AssietteFiscale assietteFiscale) {
		BigDecimal montantImposable = assietteFiscale.montantImposable();
		BigDecimal impot = BigDecimal.ZERO;
		BigDecimal plancher = BigDecimal.ZERO;

		for (Tranche tranche : TRANCHES) {
			BigDecimal montantDansTranche = tranche.montantDansLaTranche(montantImposable, plancher);
			if (montantDansTranche.signum() <= 0) {
				break;
			}
			impot = impot.add(montantDansTranche.multiply(tranche.taux()));
			plancher = tranche.plafond() == null ? montantImposable : tranche.plafond();
		}

		return Monnaie.arrondir(impot);
	}
}
