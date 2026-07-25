package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.TaxBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Assiette fiscale")
class TaxBaseCalculatorTest {

	private TaxBaseCalculator taxBaseCalculator;

	@BeforeEach
	void setUp() {
		taxBaseCalculator = new TaxBaseCalculator();
	}

	@Test
	@DisplayName("exonere integralement une prime inferieure au plafond")
	void shouldExemptTheWholeBonusUnderTheCeiling() {
		TaxBase taxBase = taxBaseCalculator.compute(
				new BigDecimal("6000000"),
				new BigDecimal("2000000"));

		assertThat(taxBase.exemptAmount()).isEqualByComparingTo("2000000");
		assertThat(taxBase.taxableAmount()).isEqualByComparingTo("4000000");
	}

	@Test
	@DisplayName("plafonne l'exoneration a cinq millions et impose le surplus")
	void shouldCapExemptionAtTheCeiling() {
		TaxBase taxBase = taxBaseCalculator.compute(
				new BigDecimal("12000000"),
				new BigDecimal("8000000"));

		assertThat(taxBase.exemptAmount()).isEqualByComparingTo("5000000");
		assertThat(taxBase.taxableAmount()).isEqualByComparingTo("7000000");
	}

	@Test
	@DisplayName("exonere encore la totalite d'une prime egale au plafond")
	void shouldExemptBonusExactlyEqualToTheCeiling() {
		// Valeur limite : 5 000 000 exactement reste integralement exonere
		TaxBase taxBase = taxBaseCalculator.compute(
				new BigDecimal("9000000"),
				new BigDecimal("5000000"));

		assertThat(taxBase.exemptAmount()).isEqualByComparingTo("5000000");
		assertThat(taxBase.taxableAmount()).isEqualByComparingTo("4000000");
	}

	@Test
	@DisplayName("n'expose aucune assiette imposable negative lorsque le brut est negatif")
	void shouldNotProduceNegativeTaxableAmount() {
		TaxBase taxBase = taxBaseCalculator.compute(
				new BigDecimal("-700000"),
				BigDecimal.ZERO);

		assertThat(taxBase.exemptAmount()).isEqualByComparingTo("0");
		assertThat(taxBase.taxableAmount()).isEqualByComparingTo("0");
	}
}
