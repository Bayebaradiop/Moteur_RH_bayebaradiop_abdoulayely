package com.company.hrsettlement.settlement.infrastructure.tax;

import com.company.hrsettlement.settlement.domain.model.Money;
import com.company.hrsettlement.settlement.domain.model.TaxBase;
import com.company.hrsettlement.settlement.domain.port.TaxAdministrationPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Adaptateur secondaire : bareme progressif applique a la part imposable.
 * <p>
 * Le detail du bareme est une preoccupation d'infrastructure : le remplacer par un
 * appel au service des impots ne demanderait aucune modification du domaine, qui ne
 * connait que {@link TaxAdministrationPort}.
 */
@Component
public class ProgressiveTaxAdministrationAdapter implements TaxAdministrationPort {

	/**
	 * Tranches annuelles, de la plus basse a la plus haute. La derniere tranche n'a
	 * pas de plafond.
	 */
	private static final List<TaxBracket> BRACKETS = List.of(
			new TaxBracket(new BigDecimal("630000"), new BigDecimal("0.00")),
			new TaxBracket(new BigDecimal("1500000"), new BigDecimal("0.20")),
			new TaxBracket(new BigDecimal("4000000"), new BigDecimal("0.30")),
			new TaxBracket(new BigDecimal("8000000"), new BigDecimal("0.35")),
			new TaxBracket(null, new BigDecimal("0.40")));

	@Override
	public BigDecimal computeTax(TaxBase taxBase) {
		BigDecimal taxableAmount = taxBase.taxableAmount();
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal lowerBound = BigDecimal.ZERO;

		for (TaxBracket bracket : BRACKETS) {
			BigDecimal amountInBracket = bracket.amountWithin(taxableAmount, lowerBound);
			if (amountInBracket.signum() <= 0) {
				break;
			}
			tax = tax.add(amountInBracket.multiply(bracket.rate()));
			lowerBound = bracket.upperBound() == null ? taxableAmount : bracket.upperBound();
		}

		return Money.scaled(tax);
	}
}
