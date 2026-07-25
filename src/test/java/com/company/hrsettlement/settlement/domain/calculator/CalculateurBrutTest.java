package com.company.hrsettlement.settlement.domain.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Montant brut")
class CalculateurBrutTest {

	private CalculateurBrut calculateurBrut;

	@BeforeEach
	void initialiser() {
		calculateurBrut = new CalculateurBrut();
	}

	@Test
	@DisplayName("additionne les indemnites puis retranche la penalite de preavis")
	void doitAdditionnerLesIndemnitesPuisRetrancherLaPenalite() {
		BigDecimal gross = calculateurBrut.calculer(
				new BigDecimal("500000"),
				new BigDecimal("300000"),
				new BigDecimal("100000"));

		assertThat(gross).isEqualByComparingTo("700000");
	}

	@Test
	@DisplayName("autorise un brut negatif lorsque la penalite depasse les indemnites")
	void doitAutoriserUnBrutNegatif() {
		BigDecimal gross = calculateurBrut.calculer(
				new BigDecimal("50000"),
				BigDecimal.ZERO,
				new BigDecimal("750000"));

		assertThat(gross).isEqualByComparingTo("-700000");
	}
}
