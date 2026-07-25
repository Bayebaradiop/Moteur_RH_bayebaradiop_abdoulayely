package com.company.hrsettlement.settlement.infrastructure.tax;

import com.company.hrsettlement.settlement.domain.model.Monnaie;
import com.company.hrsettlement.settlement.domain.model.AssietteFiscale;
import com.company.hrsettlement.settlement.domain.port.PortAdministrationFiscale;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Adaptateur secondaire : bareme progressif applique a la part imposable.
 * <p>
 * Le detail du bareme est une preoccupation d'infrastructure : le remplacer par un
 * appel au service des impots ne demanderait aucune modification du domaine, qui ne
 * connait que {@link PortAdministrationFiscale}.
 */
@Component
public class AdaptateurFiscaliteProgressive implements PortAdministrationFiscale {

	/**
	 * Tranches annuelles, de la plus basse a la plus haute. La derniere tranche n'a
	 * pas de plafond.
	 */
	private static final List<TrancheImposition> TRANCHES = List.of(
			new TrancheImposition(new BigDecimal("630000"), new BigDecimal("0.00")),
			new TrancheImposition(new BigDecimal("1500000"), new BigDecimal("0.20")),
			new TrancheImposition(new BigDecimal("4000000"), new BigDecimal("0.30")),
			new TrancheImposition(new BigDecimal("8000000"), new BigDecimal("0.35")),
			new TrancheImposition(null, new BigDecimal("0.40")));

	@Override
	public BigDecimal calculerImpot(AssietteFiscale assietteFiscale) {
		BigDecimal montantImposable = assietteFiscale.montantImposable();
		BigDecimal impot = BigDecimal.ZERO;
		BigDecimal plancher = BigDecimal.ZERO;

		for (TrancheImposition tranche : TRANCHES) {
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
