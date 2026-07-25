package com.company.hrsettlement.settlement.domain.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Montant brut")
class GrossCalculatorTest {

	private GrossCalculator grossCalculator;

	@BeforeEach
	void setUp() {
		grossCalculator = new GrossCalculator();
	}

	@Test
	@DisplayName("additionne les indemnites puis retranche la penalite de preavis")
	void shouldSumCompensationsAndSubtractPenalty() {
		BigDecimal gross = grossCalculator.compute(
				new BigDecimal("500000"),
				new BigDecimal("300000"),
				new BigDecimal("100000"));

		assertThat(gross).isEqualByComparingTo("700000");
	}

	@Test
	@DisplayName("autorise un brut negatif lorsque la penalite depasse les indemnites")
	void shouldAllowNegativeGrossWhenPenaltyExceedsCompensations() {
		BigDecimal gross = grossCalculator.compute(
				new BigDecimal("50000"),
				BigDecimal.ZERO,
				new BigDecimal("750000"));

		assertThat(gross).isEqualByComparingTo("-700000");
	}
}
