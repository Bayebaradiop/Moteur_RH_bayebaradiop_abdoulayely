package com.company.hrsettlement.settlement.infrastructure.tax;

import com.company.hrsettlement.settlement.domain.model.TaxBase;
import com.company.hrsettlement.settlement.domain.port.TaxAdministrationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Bareme fiscal progressif")
class ProgressiveTaxAdministrationAdapterTest {

	private TaxAdministrationPort taxAdministration;

	@BeforeEach
	void setUp() {
		taxAdministration = new ProgressiveTaxAdministrationAdapter();
	}

	@Test
	@DisplayName("n'impose pas un revenu contenu dans la tranche exoneree")
	void shouldNotTaxIncomeWithinTheExemptBracket() {
		BigDecimal tax = taxAdministration.computeTax(new TaxBase(new BigDecimal("500000"), BigDecimal.ZERO));

		assertThat(tax).isEqualByComparingTo("0");
	}

	@Test
	@DisplayName("applique successivement le taux de chaque tranche traversee")
	void shouldApplyEachCrossedBracketRate() {
		// 630 000 a 0 % + 870 000 a 20 % + 500 000 a 30 %
		BigDecimal tax = taxAdministration.computeTax(new TaxBase(new BigDecimal("2000000"), BigDecimal.ZERO));

		assertThat(tax).isEqualByComparingTo("324000.00");
	}

	@Test
	@DisplayName("applique le taux marginal le plus eleve a la fraction superieure")
	void shouldApplyTopRateToTheHighestFraction() {
		BigDecimal tax = taxAdministration.computeTax(new TaxBase(new BigDecimal("10000000"), BigDecimal.ZERO));

		assertThat(tax).isEqualByComparingTo("3124000.00");
	}

	@Test
	@DisplayName("n'impose jamais la part exoneree transmise par le moteur")
	void shouldNeverTaxTheExemptShare() {
		BigDecimal tax = taxAdministration.computeTax(new TaxBase(BigDecimal.ZERO, new BigDecimal("5000000")));

		assertThat(tax).isEqualByComparingTo("0");
	}
}
